package org.openjfx.controller;

import org.openjfx.model.Model;
import org.openjfx.model.ModelFile;

public interface InteractiveWindow extends Window{
    Model getModel();

    void setModel(Model model);

    ModelFile getModelFile();
    void setModelFile(ModelFile file);

}
