package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}