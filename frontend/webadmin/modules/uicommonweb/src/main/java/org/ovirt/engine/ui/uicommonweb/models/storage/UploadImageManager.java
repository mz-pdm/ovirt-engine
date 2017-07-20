package org.ovirt.engine.ui.uicommonweb.models.storage;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.ovirt.engine.core.common.action.TransferDiskImageParameters;
import org.ovirt.engine.core.common.action.TransferImageStatusParameters;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.ui.frontend.AsyncQuery;
import org.ovirt.engine.ui.uicompat.ConstantsManager;
import org.ovirt.engine.ui.uicompat.UIConstants;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;

public class UploadImageManager {

    private static final Logger log = Logger.getLogger(UploadImageManager.class.getName());

    private static UploadImageManager instance;

    public static UploadImageManager getInstance() {
        if (instance == null) {
            instance = new UploadImageManager();
        }
        return instance;
    }

    private static UIConstants constants = ConstantsManager.getInstance().getConstants();

    private Set<UploadImageHandler> uploadImageHandlers = new HashSet<>();

    public UploadImageManager() {
        setWindowClosingHandler();
    }

    /**
     * Start a new upload and register to uploads list.
     *
     * @param fileUploadElement
     *            the file upload html element
     * @param transferDiskImageParameters
     *            transfer parameters
     */
    public void startUpload(Element fileUploadElement, TransferDiskImageParameters transferDiskImageParameters) {
        UploadImageHandler uploadImageHandler = createUploadImageHandler(fileUploadElement);
        uploadImageHandlers.add(uploadImageHandler);
        uploadImageHandler.start(transferDiskImageParameters);
    }

    /**
     * Resume an existing upload.
     *
     * @param fileUploadElement
     *            the file upload html element
     * @param transferImageStatusParameters
     *            transfer parameters
     * @param asyncQuery
     *            callback to invoke
     */
    public void resumeUpload(Element fileUploadElement, TransferImageStatusParameters transferImageStatusParameters,
                             AsyncQuery<String> asyncQuery) {
        Optional<UploadImageHandler> uploadImageHandlerOptional =
            getUploadImageHandler(transferImageStatusParameters.getDiskId());
        UploadImageHandler uploadImageHandler =
            uploadImageHandlerOptional.orElseGet(() -> createUploadImageHandler(fileUploadElement));
        uploadImageHandler.resetUploadState();
        uploadImageHandlers.add(uploadImageHandler);
        uploadImageHandler.resume(transferImageStatusParameters, asyncQuery);
    }

    private UploadImageHandler createUploadImageHandler(Element fileUploadElement) {
        final UploadImageHandler uploadImageHandler = new UploadImageHandler(fileUploadElement);
        uploadImageHandler.getUploadFinishedEvent().addListener((ev, sender, args) -> {
            uploadImageHandlers.remove(uploadImageHandler);
            log.info("Removed upload handler for disk: " //$NON-NLS-1$
                    + uploadImageHandler.getDiskId().toString());
        });
        return uploadImageHandler;
    }

    private Optional<UploadImageHandler> getUploadImageHandler(Guid diskId) {
        return uploadImageHandlers.stream().filter(
            uploadImageHandler -> diskId.equals(uploadImageHandler.getDiskId())).findFirst();
    }

    /**
     * Ensures that a window closing warning is present when uploads are in progress.
     */
    private void setWindowClosingHandler() {
        Window.addWindowClosingHandler(event -> {
            boolean isAnyPolling = uploadImageHandlers.stream().anyMatch(
                    UploadImageHandler::isContinuePolling);

            if (isAnyPolling) {
                // If the window is closed, uploads will time out and pause
                event.setMessage(constants.uploadImageLeaveWindowPopupWarning());
            }
        });
    }
}
