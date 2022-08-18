package io.kotlintest.provided.com.github.funczz.kotlin.rop_fsm

import com.github.funczz.kotlin.rop.result.RopResult
import com.github.funczz.kotlin.rop.result.toResult
import com.github.funczz.kotlin.rop_fsm.*
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TransitionErrorFsmStateTest : StringSpec() {

    enum class Event {
        OnEntry, OnDo, OnExit
    }

    object StartThrowableFsmState : IFsmState<Event, Throwable> {
        override fun toTransition(event: Event): FsmTransition<Event, Throwable> = when (event) {
            Event.OnEntry -> FsmTransition.External(OnEntryThrowableFsmState)
            Event.OnDo -> FsmTransition.External(OnDoThrowableFsmState)
            Event.OnExit -> FsmTransition.External(OnExitThrowableFsmState)
        }

        override fun onEntry(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.success { ctx }
        override fun onDo(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.success { ctx }
        override fun onExit(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.success { ctx }
    }

    object OnEntryThrowableFsmState : IFsmState<Event, Throwable> {
        override fun toTransition(event: Event): FsmTransition<Event, Throwable> = when (event) {
            Event.OnEntry -> FsmTransition.External(OnEntryThrowableFsmState)
            Event.OnDo -> FsmTransition.External(OnDoThrowableFsmState)
            Event.OnExit -> FsmTransition.External(OnExitThrowableFsmState)
        }

        override fun onEntry(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.failure(ctx)
        override fun onDo(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.success { ctx }
        override fun onExit(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.success { ctx }
    }

    object OnDoThrowableFsmState : IFsmState<Event, Throwable> {
        override fun toTransition(event: Event): FsmTransition<Event, Throwable> = when (event) {
            Event.OnEntry -> FsmTransition.External(OnEntryThrowableFsmState)
            Event.OnDo -> FsmTransition.Internal()
            Event.OnExit -> FsmTransition.External(OnExitThrowableFsmState)
        }

        override fun onEntry(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.success { ctx }
        override fun onDo(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.failure(ctx)
        override fun onExit(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.success { ctx }
    }

    object OnExitThrowableFsmState : IFsmState<Event, Throwable> {
        override fun toTransition(event: Event): FsmTransition<Event, Throwable> = when (event) {
            Event.OnEntry -> FsmTransition.External(OnEntryThrowableFsmState)
            Event.OnDo -> FsmTransition.External(OnDoThrowableFsmState)
            Event.OnExit -> FsmTransition.External(OnExitThrowableFsmState)
        }

        override fun onEntry(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.success { ctx }
        override fun onDo(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.success { ctx }
        override fun onExit(event: Event, ctx: Throwable): RopResult<Throwable> = RopResult.failure(ctx)
    }

    init {

        "onEntry" {
            val (_, result) = StartThrowableFsmState.fire(Event.OnEntry, Exception("test"))
            result.toResult().exceptionOrNull()!!.also {
                (it is TransitionErrorFsmException) shouldBe true
                (it is OnEntryTransitionErrorFsmException) shouldBe true
                it.message shouldStartWith "onEntry External Transition error:"
                it.cause!!::class shouldBe Exception::class
            }
        }

        "onDo" {
            val (_, result) = StartThrowableFsmState.fire(Event.OnDo, Exception("test"))
            result.toResult().exceptionOrNull()!!.also {
                (it is TransitionErrorFsmException) shouldBe true
                (it is OnDoTransitionErrorFsmException) shouldBe true
                it.message shouldStartWith "onDo External Transition error:"
                it.cause!!::class shouldBe Exception::class
            }
        }

        "onExit" {
            val (_, result) = OnExitThrowableFsmState.fire(Event.OnExit, Exception("test"))
            result.toResult().exceptionOrNull()!!.also {
                (it is TransitionErrorFsmException) shouldBe true
                (it is OnExitTransitionErrorFsmException) shouldBe true
                it.message shouldStartWith "onExit External Transition error:"
                it.cause!!::class shouldBe Exception::class
            }
        }

        "onDo: Internal" {
            val (_, result) = OnDoThrowableFsmState.fire(Event.OnDo, Exception("test"))
            result.toResult().exceptionOrNull()!!.also {
                (it is TransitionErrorFsmException) shouldBe true
                (it is InternalTransitionErrorFsmException) shouldBe true
                it.message shouldStartWith "onDo Internal Transition error:"
                it.cause!!::class shouldBe Exception::class
            }
        }

    }
}