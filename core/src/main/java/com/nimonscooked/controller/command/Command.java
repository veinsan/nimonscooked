package com.nimonscooked.controller.command;

import com.nimonscooked.model.entity.Chef;

public interface Command {
    void execute(Chef chef);
}