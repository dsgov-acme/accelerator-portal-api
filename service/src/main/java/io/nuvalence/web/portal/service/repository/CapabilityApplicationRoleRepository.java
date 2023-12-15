package io.nuvalence.web.portal.service.repository;

import io.nuvalence.web.portal.service.domain.CapabilityApplicationRole;
import io.nuvalence.web.portal.service.domain.CapabilityApplicationRoleId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for CapabilityApplicationRole.
 */
public interface CapabilityApplicationRoleRepository
        extends CrudRepository<CapabilityApplicationRole, CapabilityApplicationRoleId> {
    List<CapabilityApplicationRole> findByApplicationRoleIn(List<String> roles);
}
