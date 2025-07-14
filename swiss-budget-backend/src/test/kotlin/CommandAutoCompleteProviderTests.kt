package com.michu_tech.swiss_budget.backend

import com.michu_tech.swiss_budget.backend.framework.definition.*
import com.michu_tech.swiss_budget.backend.framework.runtime.CommandAutoCompleteProvider
import com.michu_tech.swiss_budget.backend.framework.runtime.CommandStore
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class CommandAutoCompleteProviderTests {

    lateinit var provider: CommandAutoCompleteProvider

    @BeforeTest
    fun setup() {
        val store = CommandStore()
        store.initialize(create(Directory("", "Root", "Root Directory")) {
            add(Command("tags", "List Tags", "List all Tags")) {
                add(
                    Option(
                        "asc",
                        "ascending",
                        "Sort Ascending",
                        "Sort result ascending",
                        "How should the result be sorted?",
                        required = false,
                        type = OptionType.Flag
                    )
                )
                add(
                    Option(
                        "sc",
                        "sort-column",
                        "Sort Column",
                        "Sort result by this column",
                        "By what should your result be sorted?",
                        required = false,
                        isList = true
                    )
                )
            }

            add(Directory("tag", "Tags", "Work with Tags")) {
                add(Command("create", "Create Tag", "Create a new tag")) {
                    add(Option("n", "name", "Tag name", "The name of the tag", "What should the tag be called?"))
                    add(Option("k", "keywords", "Keywords", "The keywords of the tag", "What keywords should your tag have?"))
                }

                add(Command("rename", "Rename Tag", "Rename a tag")) {
                    add(
                        Option(
                            "id",
                            "identifier",
                            "Identifier",
                            "The identifier of the tag",
                            "What tag do you want to rename?",
                            valueSuggestionLoader = ::updateTagSuggestionLoader
                        )
                    )
                    add(Option("n", "new-name", "New name", "The new name of the tag", "What should the tag be called?"))
                }
            }
        })

        provider = CommandAutoCompleteProvider(store)
    }

    @Test
    fun testEmpty() = runTest {
        val input = ""
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(2, result.size)
        assertContains(result, "tags ")
        assertContains(result, "tag ")
    }

    @Test
    fun testTag() = runTest {
        val input = "tag"
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(2, result.size)
        assertContains(result, "tags ")
        assertContains(result, "tag ")
    }

    @Test
    fun testTagCreate() = runTest {
        val input = "tag cre"
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(1, result.size)
        assertContains(result, "tag create ")
    }

    @Test
    fun testTagCreateOptions() = runTest {
        val input = "tag create "
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(2, result.size)
        assertContains(result, "tag create --keywords=\"")
        assertContains(result, "tag create --name=\"")
    }

    @Test
    fun testTagCreateQuotedOption1() = runTest {
        val input = "tag create --name=\"micha\""
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(1, result.size)
        assertContains(result, "tag create --name=\"micha\" --keywords=\"")
    }

    @Test
    fun testTagCreateQuotedOption2() = runTest {
        val input = "tag create --name=\"micha\" "
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(1, result.size)
        assertContains(result, "tag create --name=\"micha\" --keywords=\"")
    }

    @Test
    fun testTagCreateOpenOption1() = runTest {
        val input = "tag create -n micha"
        val result = provider.getAutocompleteSuggestions(input)
        assertTrue { result.isEmpty() }
    }

    @Test
    fun testTagCreateOpenOption2() = runTest {
        val input = "tag create -n micha "
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(1, result.size)
        assertContains(result, "tag create -n micha --keywords=\"")
    }

    @Test
    fun testTagCreateOpenOption3() = runTest {
        val input = "tag create -n micha -"
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(1, result.size)
        assertContains(result, "tag create -n micha --keywords=\"")
    }

    @Test
    fun testTagCreateOpenOption4() = runTest {
        val input = "tag create -n micha --key"
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(1, result.size)
        assertContains(result, "tag create -n micha --keywords=\"")
    }

    @Test
    fun testTagCreateStartOpen() = runTest {
        val input = "tag create -n micha --keywords "
        val result = provider.getAutocompleteSuggestions(input)
        assertTrue { result.isEmpty() }
    }

    @Test
    fun testTagsFlag() = runTest {
        val input = "tags -asc "
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(1, result.size)
        assertContains(result, "tags -asc --sort-column=\"")
    }

    @Test
    fun testTagsFlagValue() = runTest {
        val input = "tags -asc=\""
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(2, result.size)
        assertContains(result, "tags -asc=\"true\"")
        assertContains(result, "tags -asc=\"false\"")
    }

    @Test
    fun testTagUpdateDynamicOptionValue() = runTest {
        val input = "tag update --new-name test name -id tr"
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(2, result.size)
        assertContains(result, "tag update --new-name test name -id tracking")
        assertContains(result, "tag update --new-name test name -id travel")
    }

    @Test
    fun testTagUpdateDynamicOptionValueQuoted() = runTest {
        val input = "tag update --new-name test name -id=\"tr"
        val result = provider.getAutocompleteSuggestions(input)
        assertEquals(2, result.size)
        assertContains(result, "tag update --new-name test name -id=\"tracking\"")
        assertContains(result, "tag update --new-name test name -id=\"travel\"")
    }

    private fun updateTagSuggestionLoader(filter: String): List<String> {
        return listOf("test", "food", "travel", "tracking", "finance", "fighting")
            .filter { it.contains(filter, true) }
    }
}