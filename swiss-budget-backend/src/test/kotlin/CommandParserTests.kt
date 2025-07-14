package com.michu_tech.swiss_budget.backend

import com.michu_tech.swiss_budget.backend.app.tag.createTag
import com.michu_tech.swiss_budget.backend.framework.definition.*
import com.michu_tech.swiss_budget.backend.framework.runtime.CommandParser
import com.michu_tech.swiss_budget.backend.framework.runtime.CommandParsingException
import com.michu_tech.swiss_budget.backend.framework.runtime.CommandStore
import com.michu_tech.swiss_budget.backend.framework.runtime.ConcreteCommand
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

class CommandParserTests {

    private lateinit var store: CommandStore
    private lateinit var parser: CommandParser

    @BeforeTest
    fun setup() {
        store = CommandStore()
        store.initialize(create(Directory("", "Root", "Root Directory")) {
            add(Command("tags", "Tags", "List all tags")) {
                add(Option("f", "filter", "Filter", "Filter result", "How do you want to filter the result?", false))
                add(Option("s", "sort", "Sort", "Sort result", "By what do you want to sort the result?", false))
                add(Option("a", "amount", "Amount", "Amount result", "Amount result", false, type = OptionType.Number))
            }

            add(Directory("tag", "Create Tag", "From here you can Manage your Tags.")) {
                add(Command("create", "Create Tag", "Creates a new tag.")) {
                    add(
                        Option(
                            "n",
                            "name",
                            "Tag name",
                            "The name of the tag to create.",
                            "What will the tag be called?",
                        )
                    )
                    add(
                        Option(
                            "k",
                            "keywords",
                            "Keywords",
                            "Keywords for the tag to be created.",
                            "What keywords should this tag have?",
                            isList = true
                        )
                    )
                    add(
                        Option(
                            "a",
                            "apply",
                            "Apply Keywords",
                            "After the tag is created, the tags of all transactions will be updated",
                            "Do you want to apply the tag?",
                            required = false,
                            defaultValue = true,
                            type = OptionType.Flag
                        )
                    )
                    add(
                        Option(
                            "sc",
                            "skip-check",
                            "Skip check",
                            "Skip some checks with this flag",
                            "What checks do you want to skip?",
                            required = false,
                            isList = true
                        )
                    )
                    action(::createTag)
                }
            }
        })

        parser = CommandParser(store)
    }

    @Test
    fun testTags() {
        val command = "tags"
        val result = parser.parse(command)
        assertEquals("Tags", result.name)
        assertEquals(3, result.missingOptions.size)
    }

    @Test
    fun testTagCreateEmpty() {
        val command = "tag create"
        val result = parser.parse(command)
        assertEquals("Create Tag", result.name)
        assertEquals(4, result.missingOptions.size)
    }

    @Test
    fun testTagCreateNameDouble() {
        val command = "tag create --name Test Command"
        val result = parser.parse(command)
        assertEquals("Create Tag", result.name)
        assertEquals(3, result.missingOptions.size)
        assertEquals(1, result.args.size)
        assertEquals("Test Command", result.args["name"])
    }

    @Test
    fun testTagCreateNameSingle() {
        val command = "tag create -n Test Command"
        val result = parser.parse(command)
        assertEquals("Create Tag", result.name)
        assertEquals(3, result.missingOptions.size)
        assertEquals(1, result.args.size)
        assertEquals("Test Command", result.args["name"])
    }

    @Test
    fun testTagCreateNameDoubleCaseIgnore() {
        val command = "tag create --NaMe Test Command"
        val result = parser.parse(command)
        assertEquals("Create Tag", result.name)
        assertEquals(3, result.missingOptions.size)
        assertEquals(1, result.args.size)
        assertEquals("Test Command", result.args["name"])
    }

    @Test
    fun testTagCreateNameSingleCaseIgnore() {
        val command = "tag create -N Test Command"
        val result = parser.parse(command)
        assertEquals("Create Tag", result.name)
        assertEquals(3, result.missingOptions.size)
        assertEquals(1, result.args.size)
        assertEquals("Test Command", result.args["name"])
    }

    @Test
    fun testTagCreateFullRequiredDouble() {
        val command = "tag create --name Test Command --keywords some,keyword, cool keyword, some more,test"
        val result = parser.parse(command)
        assertAllCreateTag(result, justRequired = true, checkSkipCheck = false, checkApply = false)
    }

    @Test
    fun testTagCreateFullRequiredDoubleEqual() {
        val command = "tag create --name=\"Test Command\" --keywords=\"some,keyword, cool keyword, some more,test\""
        val result = parser.parse(command)
        assertAllCreateTag(result, checkSkipCheck = false, checkApply = false, justRequired = true)
    }

    @Test
    fun testTagCreateFullRequiredSingle() {
        val command = "tag create -n Test Command -k some,keyword, cool keyword, some more,test"
        val result = parser.parse(command)
        assertAllCreateTag(result, checkSkipCheck = false, checkApply = false, justRequired = true)
    }

    @Test
    fun testTagCreateFullRequiredSingleEqual() {
        val command = "tag create -n=\"Test Command\" -k=\"some,keyword, cool keyword, some more,test\""
        val result = parser.parse(command)
        assertAllCreateTag(result, checkSkipCheck = false, checkApply = false, justRequired = true)
    }

    @Test
    fun testTagCreateFullDouble() {
        val command =
            "tag create --apply --name Test Command --skip-check some check,and_more --keywords some,keyword, cool keyword, some more,test,"
        val result = parser.parse(command)
        assertAllCreateTag(result)
    }

    @Test
    fun testTagCreateFullDoubleEqual() {
        val command =
            "tag create --apply --name=\"Test Command\" --skip-check=\"some check,and_more\" --keywords=\"some,keyword, cool keyword, some more,test,\""
        val result = parser.parse(command)
        assertAllCreateTag(result)
    }

    @Test
    fun testTagCreateFullSingle() {
        val command =
            "tag create -a -n Test Command -sc some check,and_more -k some,keyword, cool keyword, some more,test,"
        val result = parser.parse(command)
        assertAllCreateTag(result)
    }

    @Test
    fun testTagCreateFullMixed() {
        val command =
            "tag create -a --name=\"Test-Command\" -sc some check,and_more --keywords some,keyword, cool keyword, some more,test,"
        val result = parser.parse(command)
        assertAllCreateTag(result, tagName = "Test-Command")
    }

    @Test
    fun testTagCreateFullSingleEqual() {
        val command =
            "tag create -a -n=\"Test Command\" -sc=\"some check,and_more\" -k=\"some,keyword, cool keyword, some more,test,\""
        val result = parser.parse(command)
        assertAllCreateTag(result)
    }

    private fun assertAllCreateTag(
        result: ConcreteCommand,
        justRequired: Boolean = false,
        checkKeywords: Boolean = true,
        checkApply: Boolean = true,
        checkSkipCheck: Boolean = true,
        tagName: String = "Test Command"
    ) {
        assertEquals("Create Tag", result.name)
        assertEquals(if (justRequired) 2 else 0, result.missingOptions.size)
        assertEquals(if (justRequired) 2 else 4, result.args.size)
        assertEquals(tagName, result.args["name"])

        if (checkKeywords) {
            val keywords = result.args["keywords"]
            assertIs<List<String>>(keywords)
            assertEquals(5, keywords.size)
            assertContains(keywords, "some")
            assertContains(keywords, "keyword")
            assertContains(keywords, "cool keyword")
            assertContains(keywords, "some more")
            assertContains(keywords, "test")
        }

        if (checkApply) {
            val apply = result.args["apply"]
            assertIs<Boolean>(apply)
            assertTrue(apply)
        }

        if (checkSkipCheck) {
            val skipCheck = result.args["skip-check"]
            assertIs<List<String>>(skipCheck)
            assertEquals(2, skipCheck.size)
            assertContains(skipCheck, "some check")
            assertContains(skipCheck, "and_more")
        }
    }

    @Test
    fun testTagCreateUnknownOption() {
        val command = "tag create -a -n Test Command -quc Test"
        assertThrows<CommandParsingException> { parser.parse(command) }
    }

    // TODO if this works easily out fo the box then okay, leave it as success
    @Test
    fun testTagCreateInvalidOptionValue() {
        val command = "tag create -a -n Test-Command"
        assertThrows<CommandParsingException> { parser.parse(command) }
    }

    @Test
    fun testTagCreateOptionsAndCommandsMixed() {
        val command = "tag -a create -n Test Command"
        assertThrows<CommandParsingException> { parser.parse(command) }
    }

    @Test
    fun testTagCreateOptionValueWithDash() {
        val command = "tag create -a false -n=\"Test-Command\""
        val result = parser.parse(command)
        assertEquals(2, result.missingOptions.size)
        assertEquals(2, result.args.size)

        val apply = result.args["apply"]
        assertIs<Boolean>(apply)
        assertFalse(apply)

        val name = result.args["name"]
        assertIs<String>(name)
        assertEquals("Test-Command", name)
    }

    @Test
    fun testTagsOptionTypeNumber() {
        val command = "tags --amount 12.32"
        val result = parser.parse(command)

        val amount = result.args["amount"]
        assertIs<Double>(amount)
        assertEquals(12.32, amount)
    }
}