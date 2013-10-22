package org.ovirt.engine.api.restapi.resource;

import org.ovirt.engine.api.model.VnicProfile;
import org.ovirt.engine.api.resource.AssignedPermissionsResource;
import org.ovirt.engine.core.common.VdcObjectType;
import org.ovirt.engine.core.common.queries.GetPermissionsForObjectParameters;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.VdcQueryType;

public abstract class AbstractBackendVnicProfileResource
        extends AbstractBackendSubResource<VnicProfile, org.ovirt.engine.core.common.businessentities.network.VnicProfile> {

    protected AbstractBackendVnicProfileResource(String id) {
        super(id, VnicProfile.class,
                org.ovirt.engine.core.common.businessentities.network.VnicProfile.class,
                AbstractBackendVnicProfilesResource.SUB_COLLECTIONS);
    }

    protected VnicProfile get() {
        return performGet(VdcQueryType.GetVnicProfileById, new IdQueryParameters(guid));
    }

    protected AssignedPermissionsResource getPermissionsResource() {
        return inject(new BackendAssignedPermissionsResource(guid,
                VdcQueryType.GetPermissionsForObject,
                new GetPermissionsForObjectParameters(guid),
                VnicProfile.class,
                VdcObjectType.VnicProfile));
    }

    @Override
    protected VnicProfile doPopulate(VnicProfile model,
            org.ovirt.engine.core.common.businessentities.network.VnicProfile entity) {
        return model;
    }
}
