package com.github.funczz.kotlin.rop_fsm

/**
 * Fsm 例外クラス
 * @author funczz
 */
open class FsmException(

    /**
     * FsmException に付与するメッセージ
     */
    message: String? = null,

    /**
     * FsmException に付与する例外
     */
    cause: Throwable? = null,

    ) : Exception(message, cause) {
    companion object {
        private const val serialVersionUID: Long = 1284245738377019603L
    }
}

/**
 * Fsm 例外: イベントの処理中に例外が発生
 * @author funczz
 */
open class TransitionErrorFsmException(message: String, cause: Throwable) :
    FsmException(message = message, cause = cause) {
    companion object {
        private const val serialVersionUID: Long = -3908921195856819406L
    }
}

/**
 * Fsm 例外: (External) onEntry でイベントの処理中に例外が発生
 * @author funczz
 */
class OnEntryTransitionErrorFsmException(message: String, cause: Throwable) :
    TransitionErrorFsmException(message = message, cause = cause) {
    companion object {
        private const val serialVersionUID: Long = -5431911398468203487L
    }
}

/**
 * Fsm 例外: (External) onDo でイベントの処理中に例外が発生
 * @author funczz
 */
class OnDoTransitionErrorFsmException(message: String, cause: Throwable) :
    TransitionErrorFsmException(message = message, cause = cause) {
    companion object {
        private const val serialVersionUID: Long = 19053646042002193L
    }
}

/**
 * Fsm 例外: (External) onExit でイベントの処理中に例外が発生
 * @author funczz
 */
class OnExitTransitionErrorFsmException(message: String, cause: Throwable) :
    TransitionErrorFsmException(message = message, cause = cause) {
    companion object {
        private const val serialVersionUID: Long = 6790143830153833403L
    }
}

/**
 * Fsm 例外: (Internal) onDo でイベントの処理中に例外が発生
 * @author funczz
 */
class InternalTransitionErrorFsmException(message: String, cause: Throwable) :
    TransitionErrorFsmException(message = message, cause = cause) {
    companion object {
        private const val serialVersionUID: Long = -2030970543586560810L
    }
}

/**
 * Fsm 例外: イベント拒否
 * @author funczz
 */
class TransitionDeniedFsmException(message: String) : FsmException(message = message) {
    companion object {
        private const val serialVersionUID: Long = -6749964729286215342L
    }
}
