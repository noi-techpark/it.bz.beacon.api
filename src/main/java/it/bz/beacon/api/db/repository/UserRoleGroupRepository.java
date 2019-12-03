package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.UserRoleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRoleGroupRepository extends JpaRepository<UserRoleGroup, Long> {

    @Query(value = "SELECT * FROM user_role WHERE group_id = :groupId and user_id = :userId", nativeQuery = true)
    Optional<UserRoleGroup> findRoleByGroupAndUser(@Param("groupId") Long groupId, @Param("userId") Long userId);

    List<UserRoleGroup> findAllUserRoleGroupByGroupId(Long groupId);
}