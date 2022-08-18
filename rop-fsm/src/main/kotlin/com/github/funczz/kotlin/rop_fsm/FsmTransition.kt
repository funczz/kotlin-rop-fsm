package com.github.funczz.kotlin.rop_fsm

import java.io.Serializable

/**
 * 状態遷移の種別を定義
 * @param <EV> 発火イベントの型
 * @param <CTX> ステートマシンで処理するコンテキストの型
 * @author funczz
 */
sealed class FsmTransition<EV : Any, CTX : Any> : Serializable {

    companion object {
        private const val serialVersionUID: Long = 4719572258591593930L
    }

    /**
     * 外部遷移
     */
    class External<EV : Any, CTX : Any>(

        /**
         * 遷移先ステート
         */
        val state: IFsmState<EV, CTX>

    ) : FsmTransition<EV, CTX>() {
        companion object {
            private const val serialVersionUID: Long = 3029529208956041413L
        }
    }

    /**
     * 内部遷移
     */
    class Internal<EV : Any, CTX : Any> : FsmTransition<EV, CTX>() {
        companion object {
            private const val serialVersionUID: Long = 8724293784210391494L
        }
    }

    /**
     * 無視
     */
    class Ignore<EV : Any, CTX : Any> : FsmTransition<EV, CTX>() {
        companion object {
            private const val serialVersionUID: Long = 7277458805419095232L
        }
    }

    /**
     * 拒否
     */
    class Deny<EV : Any, CTX : Any> : FsmTransition<EV, CTX>() {
        companion object {
            private const val serialVersionUID: Long = -7790888329459600065L
        }
    }

}