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

### Rule structure
Each rule contains the following fields:
- **regex** (String): For each displayed file, the project-relative path of the file path will be tested against this regex.
  The last rule in the list that matches the file's project-relative path will be applied.
**Example**: if we want to target `PROJECT_ROOT/src/foo/bar/MyFile.cpp`, our regex will receive the string: `src/foo/bar/MyFile.cpp`.
Thus, a regex such as `src/foo/bar/MyFile\\.cpp` would work fine. A regex such as `.*\\.cpp` would also work, and match all cpp files.
To match the project root, use empty string as regex.
- **visibility** (optional): A string enum that can take the following values:
  - VISIBLE: The default value. Display the file/direcotry. If it is a directory, display its children.
  - CLOSED:  Same as VISIBLE, but if it is a directory, do not show its children.
    Usefull for very large directories that we want to prevent from opening.
  - HIDDEN:  Do not show the file/directory.
    Useful for ignoring things that do not matter.
- **color** (optional): A hex code with the color for the node. Example: `#7C66BA`
- **openColor** (optional): A hex code with the color for a directory-node, when it is open.
- **borderColor** (optional): A hex code with the color for the node's border.
- **borderWidth** (optional): A float number with the border's width in pixels.

