package com.biometryczne.signature.controllers.actions;

import javafx.scene.layout.Pane;

/**
 * Created by Michal Stasiak on 2016-01-31.
 */
public interface IControllerAction<R> {
    R perform(final Pane mainPane);
}
