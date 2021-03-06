package com.jakelauer.baseballtheater.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakelauer.baseballtheater.MainActivity
import icepick.Icepick

/**
 * Created by Jake on 10/20/2017.
 */

abstract class BaseFragment<out TData : Any> : DialogFragment()
{
	@LayoutRes
	abstract fun getLayoutResourceId(): Int

	private lateinit var m_model: TData

	fun getModel() = m_model

	override fun onCreate(savedInstanceState: Bundle?)
	{
		Icepick.restoreInstanceState(this, savedInstanceState)
		super.onCreate(savedInstanceState)
		retainInstance = true

		m_model = createModel()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
	{
		var view = inflater.inflate(getLayoutResourceId(), container, false)

		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		super.onViewCreated(view, savedInstanceState)

		beforeOnBindView()
		onBindView()

		loadData()
	}

	protected abstract fun createModel(): TData

	protected open fun onBindView()
	{

	}

	protected abstract fun loadData()

	open protected fun beforeOnBindView()
	{

	}
}