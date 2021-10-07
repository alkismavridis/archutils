# What is idea-codescape?
Idea-Codespace is an intelliJ plugin, providing a visual filesystem viewer.
Instead of a typical tree viewer, codescape displays your project as directories
inside other directories all the way down.

So, you navigate your project in a way similar to how you navigate the earth in google maps.
So you get familiar with your project visually, as you would get familiar with a landscape.

# How do I test this?
At the moment this is not yet part of the IntelliJ plugin store.
To test this, open this project with IntelliJ and run:
`./gradlew :runIde`

This will open a second intelliJ window running the plugin. Open any project and choose the "Codescape" toolbar window.

Navigate by dragging the pane, and zoom in and out. By clicking a file, it will open in the IntelliJ editor.

# What about plugin configuration?
Every project can configure how codescape behaves. Codescape will automatically check for a configuration file in:
`PROJECT_ROOT/.codescape/config.json`

The format is:
```
{
    "root": string, // optional
    "rules: CodeScapeRule[] // optional
}
```

- **root**: A path specifying the root directory that codescape will display. Default is "",
which means that codescape will display the project root. Relative paths are resolved by the project root.
- **rules**: A list of rules to apply to each file. Rules will help you ignore files and directories (for example, your build directory),
lock directories from opening (for example node_modules for performance reasons), even apply images to specific files or directories.

The strucure of each rules is:
```
class CodeScapeRule {
    /**
        Each file codescape displays will be tested with this regex (paths relative to project root!).
        If the file path matches, the rule will be applied to it.
        Examples: "frontend/node_modules", "src", "README.md" etc
    */
    regex: string;
    
    /**
        VISIBLE: Display the file/direcotry. If it is a directory, display its children.
        CLOSED: Display the file/direcotry. If it is a directory, do not show its children. Usefull for very large directories.
        HIDDEN: Do not show the file/directory. Useful for ignoring.
        Default: VISIBLE
     */
    visibility: "VISIBLE" | "CLOSED" | "HIDDEN";
    
    /** Optional. Path for an image to display. Relative paths are resolved from the project root. ~~~~*/
    image: string;
}
```
