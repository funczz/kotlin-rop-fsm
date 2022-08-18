package io.kotlintest.provided.com.github.funczz.kotlin.rop_fsm

import com.github.funczz.kotlin.rop.result.toResult
import com.github.funczz.kotlin.rop_fsm.TransitionDeniedFsmException
import io.kotlintest.TestCase
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.provided.com.github.funczz.kotlin.rop_fsm.toggle.OffToggleState
import io.kotlintest.provided.com.github.funczz.kotlin.rop_fsm.toggle.OnToggleState
import io.kotlintest.provided.com.github.funczz.kotlin.rop_fsm.toggle.ToggleContext
import io.kotlintest.provided.com.github.funczz.kotlin.rop_fsm.toggle.ToggleEvent
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ToggleFsmStateTest : StringSpec() {

    private lateinit var offContext: ToggleContext

    private lateinit var onContext: ToggleContext

    override fun beforeTest(testCase: TestCase) {
        offContext = ToggleContext()
        onContext = ToggleContext(true, 2)
        super.beforeTest(testCase)
    }

    /**
     * OffToggleState
     */
    init {

        "Off, event: TurnOff" {
            OffToggleState.isDeny(ToggleEvent.TurnOff) shouldBe true
            OffToggleState.isExternal(ToggleEvent.TurnOff) shouldBe false
            OffToggleState.isInternal(ToggleEvent.TurnOff) shouldBe false
            OffToggleState.isIgnore(ToggleEvent.TurnOff) shouldBe false
        }

        "Off, event: TurnOn" {
            // ToggleEvent.TurnOn
            OffToggleState.isDeny(ToggleEvent.TurnOn) shouldBe false
            OffToggleState.isExternal(ToggleEvent.TurnOn) shouldBe true
            OffToggleState.isInternal(ToggleEvent.TurnOn) shouldBe false
            OffToggleState.isIgnore(ToggleEvent.TurnOn) shouldBe false
        }

        "Off, event: Internal" {
            OffToggleState.isDeny(ToggleEvent.Internal) shouldBe false
            OffToggleState.isExternal(ToggleEvent.Internal) shouldBe false
            OffToggleState.isInternal(ToggleEvent.Internal) shouldBe true
            OffToggleState.isIgnore(ToggleEvent.Internal) shouldBe false
        }

        "Off, event: Ignore" {
            OffToggleState.isDeny(ToggleEvent.Ignore) shouldBe false
            OffToggleState.isExternal(ToggleEvent.Ignore) shouldBe false
            OffToggleState.isInternal(ToggleEvent.Ignore) shouldBe false
            OffToggleState.isIgnore(ToggleEvent.Ignore) shouldBe true
        }

        "Off --TurnOn--> RopResult.success: On" {
            val (state, actual) = OffToggleState.fire(ToggleEvent.TurnOn, offContext)
            state::class shouldBe OnToggleState::class
            actual.isSuccess shouldBe true
            actual.getOrThrow().also { ctx ->
                ctx.isOn shouldBe true
                ctx.count shouldBe 1
            }
            offContext.count shouldBe 1
        }

        "Off --TurnOff--> RopResult.failure" {
            val (_, actual) = OffToggleState.fire(ToggleEvent.TurnOff, offContext)
            actual.isSuccess shouldBe false
            actual.toResult().exceptionOrNull()!!.also {
                (it is TransitionDeniedFsmException) shouldBe true
                it.message shouldStartWith "Transition denied:"
            }
            offContext.count shouldBe 0
        }

        "Off --Ignore--> RopResult.success: Off" {
            val (state, actual) = OffToggleState.fire(ToggleEvent.Ignore, offContext)
            state::class shouldBe OffToggleState::class
            actual.isSuccess shouldBe true
            actual.getOrThrow().also { ctx ->
                ctx.isOn shouldBe false
                ctx.count shouldBe 0
            }
            offContext.count shouldBe 0
        }

        "Off --Internal--> RopResult.success: Off" {
            val (state, actual) = OffToggleState.fire(ToggleEvent.Internal, offContext)
            state::class shouldBe OffToggleState::class
            actual.isSuccess shouldBe true
            actual.getOrThrow().also { ctx ->
                ctx.isOn shouldBe false
                ctx.count shouldBe 1
            }
            offContext.count shouldBe 1
        }

    }

    /**
     * OnToggleState
     */
    init {
        "On, event: TurnOff" {
            OnToggleState.isDeny(ToggleEvent.TurnOff) shouldBe false
            OnToggleState.isExternal(ToggleEvent.TurnOff) shouldBe true
            OnToggleState.isInternal(ToggleEvent.TurnOff) shouldBe false
            OnToggleState.isIgnore(ToggleEvent.TurnOff) shouldBe false
        }

        "On, event: TurnOn" {
            // ToggleEvent.TurnOn
            OnToggleState.isDeny(ToggleEvent.TurnOn) shouldBe true
            OnToggleState.isExternal(ToggleEvent.TurnOn) shouldBe false
            OnToggleState.isInternal(ToggleEvent.TurnOn) shouldBe false
            OnToggleState.isIgnore(ToggleEvent.TurnOn) shouldBe false
        }

        "On, event: Internal" {
            OnToggleState.isDeny(ToggleEvent.Internal) shouldBe false
            OnToggleState.isExternal(ToggleEvent.Internal) shouldBe false
            OnToggleState.isInternal(ToggleEvent.Internal) shouldBe true
            OnToggleState.isIgnore(ToggleEvent.Internal) shouldBe false
        }

        "On, event: Ignore" {
            OnToggleState.isDeny(ToggleEvent.Ignore) shouldBe false
            OnToggleState.isExternal(ToggleEvent.Ignore) shouldBe false
            OnToggleState.isInternal(ToggleEvent.Ignore) shouldBe false
            OnToggleState.isIgnore(ToggleEvent.Ignore) shouldBe true
        }

        "On --TurnOff--> RopResult.success: Off" {
            val (state, actual) = OnToggleState.fire(ToggleEvent.TurnOff, onContext)
            state::class shouldBe OffToggleState::class
            actual.isSuccess shouldBe true
            actual.getOrThrow().also { ctx ->
                ctx.isOn shouldBe false
                ctx.count shouldBe 1
            }
            onContext.count shouldBe 1
        }

        "On --TurnOn--> RopResult.failure" {
            val (_, actual) = OnToggleState.fire(ToggleEvent.TurnOn, onContext)
            actual.isSuccess shouldBe false
            actual.toResult().exceptionOrNull()!!.also {
                (it is TransitionDeniedFsmException) shouldBe true
                it.message shouldStartWith "Transition denied:"
            }
            onContext.count shouldBe 2
        }

        "On --Ignore--> RopResult.success: On" {
            val (state, actual) = OnToggleState.fire(ToggleEvent.Ignore, onContext)
            state::class shouldBe OnToggleState::class
            actual.isSuccess shouldBe true
            actual.getOrThrow().also { ctx ->
                ctx.isOn shouldBe true
                ctx.count shouldBe 2
            }
            onContext.count shouldBe 2
        }

        "On --Internal--> RopResult.success: On" {
            val (state, actual) = OnToggleState.fire(ToggleEvent.Internal, onContext)
            state::class shouldBe OnToggleState::class
            actual.isSuccess shouldBe true
            actual.getOrThrow().also { ctx ->
                ctx.isOn shouldBe true
                ctx.count shouldBe 3
            }
            onContext.count shouldBe 3
        }

    }

}