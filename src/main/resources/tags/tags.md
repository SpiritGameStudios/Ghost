If you find yourself repeating the same thing over and over while answering questions, you can make a PR for [Ghost](https://github.com/SpiritGameStudios/Ghost) to add a tag which can be used to quickly reference the answer in the future.

To create a tag, add a new markdown file to the `src/main/resources/tags` directory. The file should be named after the tag you want to create. The file should contain the content you want to display when the tag is used.
You can also add an alias by creating a file with the extension `.alias` and the same name as the tag file. The alias file should contain the name of the tag you want to create an alias for. Make sure that there are no empty lines in the alias file.

Tags are used by typing `/tag` followed by the tag name. For example, `/tag tags` will display this page.
