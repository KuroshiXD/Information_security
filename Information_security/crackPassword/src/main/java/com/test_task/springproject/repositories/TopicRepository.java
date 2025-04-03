package com.test_task.springproject.repositories;

import com.test_task.springproject.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Iterable<Topic> findByAuthorId(Long authorId);
}
