# What is idea-codescape?
Idea-Codespace is an intelliJ plugin, providing a visual filesystem viewer.
Instead of a typical tree viewer, codescape displays your project as directories
inside other directories all the way down.

So, you navigate your project in a way similar to how you navigate the earth in google maps.
This way, you get familiar with your project visually, like you get familiar with a landscape.
You learn its regions visually - a way I hope is natural and intuitive to the human mind.

# How do I test this?
Codescape is not yet part of the IntelliJ plugin store.
To test this, open this project with IntelliJ and run:

`./gradlew :runIde`

This command will open a second intelliJ window running the plugin. Open any project and click on the "Codescape" toolbar window.

Explore your project by dragging the pane, and zoom in and out. Similar to how google maps works.
Click a file to open it in the IntelliJ editor.

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

The structure of each rule is:
```
{
    /**
        For each file codescape displays, its project-relative path will be tested against the regex of each rule.
        The last rule that matches the file's project-relative path will be applied.
        
        Example: if you want to target "PROJECT_ROOT/src/foo/bar/MyFile.cpp"
        your regex will receive the string: "src/foo/bar/MyFile.cpp".
        So, a regex such as "src/foo/bar/MyFile\\.cpp" would work.
        A regex such as ".*\\.cpp" would match all cpp files.
    */
    regex: string;
    
    /**
        Optional. Default is VISIBLE.
        - VISIBLE: Display the file/direcotry. If it is a directory, display its children.
        - CLOSED:  Same as VISIBLE, but if it is a directory, do not show its children.
                   Usefull for very large directories that we want to prevent from opening.
        - HIDDEN:  Do not show the file/directory.
                   Useful for ignoring things that do not matter.
     */
    visibility: "VISIBLE" | "CLOSED" | "HIDDEN";
    
    /**
        Optional. Default is nullPath for an image to display.
        Relative paths are resolved from the project root.
    */
    image: string?;
    
    /** Any valid rgb or rgba value. Example: "#F3D277" */
    color: string?
}
```
