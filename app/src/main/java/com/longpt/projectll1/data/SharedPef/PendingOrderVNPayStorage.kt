package com.longpt.projectll1.data.SharedPef

import android.content.Context
import com.google.gson.Gson
import com.longpt.projectll1.domain.model.Order
import androidx.core.content.edit

object PendingOrderStorage {
    private const val PREFS_NAME = "pending_order_prefs"
    private const val KEY_PENDING_ORDER = "pending_order"

    fun saveOrder(context: Context, order: Order) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(order)
        prefs.edit { putString(KEY_PENDING_ORDER, json) }
    }

    fun getOrder(context: Context): Order? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PENDING_ORDER, null) ?: return null
        return Gson().fromJson(json, Order::class.java)
    }

    fun clearOrder(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { remove(KEY_PENDING_ORDER) }
    }
}
