package com.biometryczne.signature.controllers.actions;

import javafx.scene.layout.Pane;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by c309044 on 2016-05-10.
 */
public class ActionsTest {

    @Test
    public void testStringResponse() {
        final String expected = "This is expected";
        ControllerActionManager<String> controllerActionManager = new ControllerActionManager<String>();
        controllerActionManager.setPerformer(mainPane -> expected);

        String response = controllerActionManager.perform(new Pane());
        assertEquals(expected, response);
    }

    @Test
    public void testVoidResponse() {
        ControllerActionManager controllerActionManager = new ControllerActionManager();

        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        controllerActionManager.setPerformer(mainPane -> {

            atomicBoolean.getAndSet(true);

            return null;
        });

        controllerActionManager.perform(new Pane());

        assertEquals(true, atomicBoolean.get());
    }

}
