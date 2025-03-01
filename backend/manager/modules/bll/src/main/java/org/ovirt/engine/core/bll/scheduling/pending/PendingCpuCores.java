package org.ovirt.engine.core.bll.scheduling.pending;

import org.ovirt.engine.core.bll.scheduling.utils.VmSpecificPendingResourceEqualizer;
import org.ovirt.engine.core.common.businessentities.CpuPinningPolicy;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.compat.Guid;

/**
 * Represents a cpu core allocation that is going to be used
 * by not yet started VM on a specified host
 */
public class PendingCpuCores extends PendingResource {

    private CpuPinningPolicy cpuPinningPolicy;

    private int coreCount;

    public PendingCpuCores(VDS host, VM vm, int coreCount) {
        super(host, vm);
        this.cpuPinningPolicy = vm.getCpuPinningPolicy();
        this.coreCount = coreCount;
        this.cpuPinningPolicy = vm.getCpuPinningPolicy();
    }

    public PendingCpuCores(Guid host, VM vm, int coreCount) {
        super(host, vm);
        this.cpuPinningPolicy = vm.getCpuPinningPolicy();
        this.coreCount = coreCount;
        this.cpuPinningPolicy = vm.getCpuPinningPolicy();
    }

    public long getCoreCount() {
        return coreCount;
    }

    public void setCoreCount(int coreCount) {
        this.coreCount = coreCount;
    }

    public CpuPinningPolicy getCpuPinningPolicy() {
        return cpuPinningPolicy;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object other) {
        return VmSpecificPendingResourceEqualizer.isEqual(this, other);
    }

    @Override
    public int hashCode() {
        return VmSpecificPendingResourceEqualizer.calcHashCode(this);
    }

    public static int collectForHost(PendingResourceManager manager, Guid host) {
        int sum = 0;
        for (PendingCpuCores resource: manager.pendingHostResources(host, PendingCpuCores.class)) {
            sum += resource.getCoreCount();
        }

        return sum;
    }

    public static int collectSharedForHost(PendingResourceManager manager, Guid host) {
        int sum = 0;
        for (PendingCpuCores resource: manager.pendingHostResources(host, PendingCpuCores.class)) {
            if (!resource.getCpuPinningPolicy().isExclusive()) {
                sum += resource.getCoreCount();
            }
        }

        return sum;
    }
}
