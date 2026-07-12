package com.todo_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.todo_api.dto.TaskRequest;
import com.todo_api.entity.Task;
import com.todo_api.entity.User;
import com.todo_api.entity.enums.Priority;
import com.todo_api.entity.enums.Status;
import com.todo_api.exception.TaskNotFoundException;
import com.todo_api.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    @Test
    void create_setsOwnerAndSavesTask() {
        TaskRequest request = new TaskRequest("Buy milk", "2 liters", Priority.LOW, Status.PENDING);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.create(user, request);

        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getTitle()).isEqualTo("Buy milk");
        assertThat(result.getDescription()).isEqualTo("2 liters");
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
        assertThat(result.getStatus()).isEqualTo(Status.PENDING);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void findAll_withoutFilters_delegatesToFindAllByUserId() {
        when(taskRepository.findAllByUserId(1L)).thenReturn(List.of(new Task()));

        List<Task> result = taskService.findAll(user, null, null);

        assertThat(result).hasSize(1);
        verify(taskRepository).findAllByUserId(1L);
        verify(taskRepository, never()).findAllByUserIdAndStatus(any(), any());
    }

    @Test
    void findAll_withStatusOnly_delegatesToFindAllByUserIdAndStatus() {
        when(taskRepository.findAllByUserIdAndStatus(1L, Status.DONE)).thenReturn(List.of(new Task()));

        List<Task> result = taskService.findAll(user, Status.DONE, null);

        assertThat(result).hasSize(1);
        verify(taskRepository).findAllByUserIdAndStatus(1L, Status.DONE);
    }

    @Test
    void findAll_withPriorityOnly_delegatesToFindAllByUserIdAndPriority() {
        when(taskRepository.findAllByUserIdAndPriority(1L, Priority.HIGH)).thenReturn(List.of(new Task()));

        List<Task> result = taskService.findAll(user, null, Priority.HIGH);

        assertThat(result).hasSize(1);
        verify(taskRepository).findAllByUserIdAndPriority(1L, Priority.HIGH);
    }

    @Test
    void findAll_withStatusAndPriority_delegatesToCombinedQuery() {
        when(taskRepository.findAllByUserIdAndStatusAndPriority(1L, Status.PENDING, Priority.LOW))
                .thenReturn(List.of(new Task()));

        List<Task> result = taskService.findAll(user, Status.PENDING, Priority.LOW);

        assertThat(result).hasSize(1);
        verify(taskRepository).findAllByUserIdAndStatusAndPriority(1L, Status.PENDING, Priority.LOW);
    }

    @Test
    void findById_whenOwnedByUser_returnsTask() {
        Task task = new Task();
        task.setId(10L);
        when(taskRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(task));

        Task result = taskService.findById(user, 10L);

        assertThat(result).isSameAs(task);
    }

    @Test
    void findById_whenNotFoundOrNotOwned_throwsTaskNotFoundException() {
        when(taskRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(user, 10L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void update_appliesChangesAndSaves() {
        Task task = new Task();
        task.setId(10L);
        task.setTitle("Old title");
        when(taskRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskRequest request = new TaskRequest("New title", "New description", Priority.HIGH, Status.DONE);
        Task result = taskService.update(user, 10L, request);

        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.getDescription()).isEqualTo("New description");
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(result.getStatus()).isEqualTo(Status.DONE);
        verify(taskRepository).save(task);
    }

    @Test
    void update_whenTaskNotOwnedByUser_throwsAndNeverSaves() {
        when(taskRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        TaskRequest request = new TaskRequest("New title", null, Priority.HIGH, Status.DONE);

        assertThatThrownBy(() -> taskService.update(user, 10L, request))
                .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void delete_whenOwnedByUser_deletesTask() {
        Task task = new Task();
        task.setId(10L);
        when(taskRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(task));

        taskService.delete(user, 10L);

        verify(taskRepository).delete(task);
    }

    @Test
    void delete_whenNotOwnedByUser_throwsAndNeverDeletes() {
        when(taskRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(user, 10L))
                .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, never()).delete(any());
    }
}
