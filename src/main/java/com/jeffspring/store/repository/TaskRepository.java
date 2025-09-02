package com.jeffspring.store.repository;

import com.jeffspring.store.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List <Task> findAllByCreatedByOrderByCompletedAscDueDateAscIdAsc(String createdBy);

    List<Task> findByCreatedByAndCompletedOrderByDueDateAscIdAsc(String createdBy, boolean completed);

    Optional<Task> findByIdAndCreatedBy(Long id,String createdBy);

}
