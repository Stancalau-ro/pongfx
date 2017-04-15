package ro.stancalau.pong.gui;

abstract class Modal extends Presentation {

    private ModalDialog dialog;

    public Modal(ScreensConfig config) {
        super(config);
    }

    public void setDialog(ModalDialog dialog) {
        this.dialog = dialog;
    }
}