package com.biometryczne.signature.controllers.actions;

import javafx.scene.layout.Pane;

/**
 * Created by Michal Stasiak on 2016-01-31.
 */
public class ControllerActionManager implements IControllerAction {

    private IControllerAction controllerActionItem;

    public void setPerformer(final IControllerAction controllerActionItem) {
        this.controllerActionItem = controllerActionItem;
    }

    @Override
    public void perform(Pane mainPane) {
        controllerActionItem.perform(mainPane);
    }
}
