// -----BEGIN DISCLAIMER-----
/*******************************************************************************
 * Copyright (c) 2010 JCrypTool team and contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// -----END DISCLAIMER-----
package org.jcryptool.commands.core.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcryptool.commands.core.Command;
import org.jcryptool.core.operations.editors.EditorsManager;

public abstract class AbstractCommand implements Command {
	private static final String EDITORINPUT_OPTIONNAME = "ed";
	private static final String EDITORINPUT_LONG_OPTIONNAME = "editor";
	private static final String FILEINPUT_OPTIONNAME = "f";
	private static final String FILEINPUT_LONG_OPTIONNAME = "inputFile";
	private static final String FILEINPUT_ARGUMENT1_NAME = "FILE_PATH";
	private static final String TEXTINPUT_OPTIONNAME = "t";
	private static final String TEXTINPUT_LONG_OPTIONNAME = "inputText";
	private static final String TEXTINPUT_ARGUMENT1_NAME = "TEXT";
	private String description = ""; //$NON-NLS-1$
    private String commandName = ""; //$NON-NLS-1$
    private String commandSyntax = ""; //$NON-NLS-1$

    public final String getCommandName() {
        return commandName;
    }

    public final String getDescription() {
        return description;
    }

    public final String getCommandSyntax() {
        return commandSyntax;
    }

    public final void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final void setCommandSyntax(String commandSyntax) {
        this.commandSyntax = commandSyntax;
    }

    @SuppressWarnings("static-access")
    protected void createInputOptions(Options options) {
        OptionGroup inputMode = new OptionGroup();

        inputMode.addOption(OptionBuilder.withLongOpt(TEXTINPUT_LONG_OPTIONNAME).hasArg(true) //$NON-NLS-1$
                .withArgName(TEXTINPUT_ARGUMENT1_NAME).withDescription( //$NON-NLS-1$
                        Messages.AbstractCommand_0).create(TEXTINPUT_OPTIONNAME)); //$NON-NLS-1$

        inputMode.addOption(OptionBuilder.withLongOpt(FILEINPUT_LONG_OPTIONNAME).hasArg(true) //$NON-NLS-1$
                .withArgName(FILEINPUT_ARGUMENT1_NAME).withDescription(Messages.AbstractCommand_1) //$NON-NLS-1$
                .create(FILEINPUT_OPTIONNAME)); //$NON-NLS-1$

        inputMode.addOption(OptionBuilder.withLongOpt(EDITORINPUT_LONG_OPTIONNAME) //$NON-NLS-1$
                .hasArg(false).withDescription(Messages.AbstractCommand_2).create(EDITORINPUT_OPTIONNAME)); //$NON-NLS-1$

        inputMode.setRequired(true);

        options.addOptionGroup(inputMode);
    }

    protected InputStream handleInputOption(CommandLine commandLine) throws FileNotFoundException, ParseException {
        InputStream inputStream = null;
        if (commandLine.hasOption(TEXTINPUT_OPTIONNAME)) { //$NON-NLS-1$
            String input = commandLine.getOptionValue(TEXTINPUT_OPTIONNAME); //$NON-NLS-1$
            inputStream = new BufferedInputStream(new ByteArrayInputStream(input.getBytes()));
            try {
                if (inputStream.available() < 1)
                    throw new ParseException(Messages.AbstractCommand_zerotextMsg);
            } catch (IOException e) {
                throw new ParseException(Messages.AbstractCommand_manualInputException);
            }
        } else if (commandLine.hasOption(EDITORINPUT_OPTIONNAME)) { //$NON-NLS-1$
            inputStream = EditorsManager.getInstance().getActiveEditorContentInputStream();

            if (inputStream == null)
                throw new ParseException(Messages.AbstractCommand_noEditorMsg);
        } else if (commandLine.hasOption(FILEINPUT_OPTIONNAME)) { //$NON-NLS-1$
            String fileName = commandLine.getOptionValue(FILEINPUT_OPTIONNAME); //$NON-NLS-1$
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
        }

        return inputStream;
    }

	public void execute(CommandLine commandLine) throws IllegalCommandException {
		
	}
    
    
}
