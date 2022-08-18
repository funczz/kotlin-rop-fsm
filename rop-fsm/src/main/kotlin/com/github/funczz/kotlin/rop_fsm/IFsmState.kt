package com.github.funczz.kotlin.rop_fsm

import com.github.funczz.kotlin.rop.result.RopResult
import com.github.funczz.kotlin.rop.result.fold

/**
 * インターフェイス
 * ステートを定義
 * @param <EV> 発火イベントの型
 * @param <CTX> ステートマシンで処理するコンテキストの型
 * @author funczz
 */
interface IFsmState<EV : Any, CTX : Any> {

    /**
     * 渡されたイベントの遷移先を返す
     * @param event イベント
     * @return <code>FsmTransition</code>
     */
    fun toTransition(event: EV): FsmTransition<EV, CTX>

    /**
     * 別ステートから外部遷移した際に実行する処理
     * @param event イベント
     * @param ctx コンテキスト
     * @return RopResult コンテキスト
     */
    fun onEntry(event: EV, ctx: CTX): RopResult<CTX>

    /**
     * このステートに遷移した際に実行する処理
     * @param event イベント
     * @param ctx コンテキスト
     * @return RopResult コンテキスト
     */
    fun onDo(event: EV, ctx: CTX): RopResult<CTX>

    /**
     * 別ステートに外部遷移する際に実行する処理
     * @param event イベント
     * @param ctx コンテキスト
     * @return RopResult コンテキスト
     */
    fun onExit(event: EV, ctx: CTX): RopResult<CTX>

    /**
     * イベントが拒否されるかを返す
     * @param event イベント
     * @return 拒否するイベントなら <code>true</code> 、
     *         それ以外は <code>false</code>
     */
    fun isDeny(event: EV): Boolean {
        return this.toTransition(event = event) is FsmTransition.Deny
    }

    /**
     * イベントが外部遷移するかを返す
     * @param event イベント
     * @return 外部遷移するイベントなら <code>true</code> 、
     *         それ以外は <code>false</code>
     */
    fun isExternal(event: EV): Boolean {
        return this.toTransition(event = event) is FsmTransition.External
    }

    /**
     * イベントが内部遷移するかを返す
     * @param event イベント
     * @return 内部遷移するイベントなら <code>true</code> 、
     *         それ以外は <code>false</code>
     */
    fun isInternal(event: EV): Boolean {
        return this.toTransition(event = event) is FsmTransition.Internal
    }

    /**
     * イベントが無視されるかを返す
     * @param event イベント
     * @return 無視するイベントなら <code>true</code> 、
     *         それ以外は <code>false</code>
     */
    fun isIgnore(event: EV): Boolean {
        return this.toTransition(event = event) is FsmTransition.Ignore
    }

    /**
     * イベントを発火する
     * @param event イベント
     * @param ctx コンテキスト
     * @return Pair ステートと、コンテキストの <code>RopResult</code> を保持する。
     */
    fun fire(event: EV, ctx: CTX): Pair<IFsmState<EV, CTX>, RopResult<CTX>> {
        return when (val transition = this.toTransition(event = event)) {
            is FsmTransition.Deny -> {
                return createFailureValue(
                    prefix = "Transition denied",
                    state = this,
                    event = event,
                ) { message, _ ->
                    TransitionDeniedFsmException(message)
                }
            }

            is FsmTransition.External -> {
                var currentState = this

                var nextCtx = currentState.onExit(event, ctx)
                    .fold(
                        failure = {
                            return createFailureValue(
                                prefix = "onExit External Transition error",
                                state = currentState,
                                event = event,
                                cause = it,
                            ) { message, cause ->
                                OnExitTransitionErrorFsmException(message, cause!!)
                            }
                        },
                        success = {
                            it
                        }
                    )

                currentState = transition.state

                nextCtx = currentState.onEntry(event, nextCtx)
                    .fold(
                        failure = {
                            return createFailureValue(
                                prefix = "onEntry External Transition error",
                                state = currentState,
                                event = event,
                                cause = it,
                            ) { message, cause ->
                                OnEntryTransitionErrorFsmException(message, cause!!)
                            }
                        },
                        success = {
                            it
                        }
                    )

                nextCtx = currentState.onDo(event, nextCtx)
                    .fold(
                        failure = {
                            return createFailureValue(
                                prefix = "onDo External Transition error",
                                state = currentState,
                                event = event,
                                cause = it,
                            ) { message, cause ->
                                OnDoTransitionErrorFsmException(message, cause!!)
                            }
                        },
                        success = {
                            it
                        }
                    )
                createSuccessValue(state = currentState, ctx = nextCtx)
            }

            is FsmTransition.Internal -> {
                this.onDo(event, ctx)
                    .fold(
                        failure = {
                            return createFailureValue(
                                prefix = "onDo Internal Transition error",
                                state = this,
                                event = event,
                                cause = it,
                            ) { message, cause ->
                                InternalTransitionErrorFsmException(message, cause!!)
                            }
                        },
                        success = {
                            createSuccessValue(state = this, ctx = it)
                        }
                    )
            }

            is FsmTransition.Ignore -> {
                createSuccessValue(state = this, ctx = ctx)
            }
        }
    }

    private fun createFailureValue(
        prefix: String,
        state: IFsmState<EV, CTX>,
        event: EV,
        cause: Throwable? = null,
        error: (message: String, cause: Throwable?) -> Throwable
    ): Pair<IFsmState<EV, CTX>, RopResult<CTX>> = Pair(
        state,
        RopResult.failure(
            error(
                "%s: State=%s, Event=%s".format(
                    prefix,
                    state::class.qualifiedName,
                    event::class.qualifiedName
                ),
                cause
            )
        )
    )

    private fun createSuccessValue(
        state: IFsmState<EV, CTX>,
        ctx: CTX
    ): Pair<IFsmState<EV, CTX>, RopResult<CTX>> = Pair(
        state,
        RopResult.tee { ctx }
    )

}