package io.kotlintest.provided.com.github.funczz.kotlin.rop_fsm.toggle

import com.github.funczz.kotlin.rop.result.RopResult
import com.github.funczz.kotlin.rop_fsm.FsmTransition
import com.github.funczz.kotlin.rop_fsm.IFsmState

object OffToggleState : IFsmState<ToggleEvent, ToggleContext> {
    override fun toTransition(event: ToggleEvent): FsmTransition<ToggleEvent, ToggleContext> {
        return when (event) {
            is ToggleEvent.TurnOn -> FsmTransition.External(OnToggleState)
            is ToggleEvent.TurnOff -> FsmTransition.Deny()
            is ToggleEvent.Ignore -> FsmTransition.Ignore()
            is ToggleEvent.Internal -> FsmTransition.Internal()
        }
    }

    override fun onEntry(event: ToggleEvent, ctx: ToggleContext): RopResult<ToggleContext> {
        return RopResult.tee {
            ctx.isOn = false
            ctx.count = 0
            ctx
        }
    }

    override fun onDo(event: ToggleEvent, ctx: ToggleContext): RopResult<ToggleContext> {
        return RopResult.tee {
            ctx.count += 1
            ctx
        }
    }

    override fun onExit(event: ToggleEvent, ctx: ToggleContext): RopResult<ToggleContext> {
        return RopResult.tee {
            ctx
        }
    }
}