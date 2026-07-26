package com.mx.tecdesoftware.Pendix.domain.repository;

import com.mx.tecdesoftware.Pendix.domain.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    List<Project> getAll();

    Optional<Project> getById(Integer projectId);

    List<Project> getByOwnerId(Integer ownerId);

    Project save(Project project);

    void deleteById(Integer projectId);
}
