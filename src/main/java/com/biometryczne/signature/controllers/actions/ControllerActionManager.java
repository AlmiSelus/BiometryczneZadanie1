package com.biometryczne.signature.controllers.actions;

import javafx.scene.layout.Pane;

/**
 * Created by Michal Stasiak on 2016-01-31.
 */
public class ControllerActionManager<R> implements IControllerAction<R> {

    private IControllerAction<R> controllerActionItem;

    public void setPerformer(final IControllerAction controllerActionItem) {
        this.controllerActionItem = controllerActionItem;
    }

    @Override
    public R perform(Pane mainPane) {
        return controllerActionItem.perform(mainPane);
    }
}
