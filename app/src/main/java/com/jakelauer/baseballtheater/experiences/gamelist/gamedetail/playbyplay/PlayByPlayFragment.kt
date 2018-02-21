package com.jakelauer.baseballtheater.experiences.gamelist.gamedetail.playbyplay

import com.jakelauer.baseballtheater.MlbDataServer.DataStructures.GameSummary
import com.jakelauer.baseballtheater.MlbDataServer.DataStructures.Innings.InningHalf
import com.jakelauer.baseballtheater.MlbDataServer.DataStructures.Innings.PlayByPlay
import com.jakelauer.baseballtheater.MlbDataServer.GameDetailCreator
import com.jakelauer.baseballtheater.R
import com.jakelauer.baseballtheater.base.RefreshableListFragment
import com.jakelauer.baseballtheater.base.Syringe
import com.jakelauer.baseballtheater.common.listitems.HeaderItem

/**
 * Created by Jake on 2/7/2018.
 */
class PlayByPlayFragment : RefreshableListFragment<PlayByPlayFragment.Model>()
{
	var m_game: GameSummary by Syringe()
	var m_expandedItem: BatterItem? = null

	override fun getLayoutResourceId() = R.layout.play_by_play_fragment

	override fun createModel(): Model = Model()

	override fun loadData()
	{
		m_refreshView.isRefreshing = true

		val gdc = GameDetailCreator(m_game.gameDataDirectory, false)
		try
		{
			gdc.getInnings {
				getModel().updatePlayByPlay(it)
				onDataLoaded()
				m_refreshView.isRefreshing = false
			}
		}
		catch (e: Exception)
		{
			e.printStackTrace()
			m_refreshView.isRefreshing = false
		}
	}

	private fun onDataLoaded()
	{
		m_adapter?.clear()

		val playByPlayData = getModel().m_playByPlay
		if (playByPlayData != null)
		{
			renderInnings(playByPlayData)
		}
	}

	private fun renderInnings(playByPlayData: PlayByPlay)
	{
		val context = context ?: throw Exception("Context cannot be null")

		val inningsDec = playByPlayData.innings.sortedByDescending { a -> a.num }

		for (inning in inningsDec)
		{
			val topHeader = context.getString(R.string.GAME_DETAIL_inning_header_top, inning.num)
			val bottomHeader = context.getString(R.string.GAME_DETAIL_inning_header_bottom, inning.num)

			m_adapter?.add(HeaderItem(bottomHeader))
			renderHalfInning(inning.bottom)

			m_adapter?.add(HeaderItem(topHeader))
			renderHalfInning(inning.top)
		}
	}

	private fun renderHalfInning(halfInning: InningHalf)
	{
		val context = context ?: throw Exception("Context cannot be null")

		for (batter in halfInning.atbat)
		{
			val listItem = BatterItem(batter)

			listItem.setResultClickListener({ _, _ ->
				m_expandedItem?.let { expandedItem ->
					if (expandedItem != listItem)
					{
						expandedItem.toggleExpanded(context, false)
					}
				}
				m_expandedItem = listItem
			})

			m_adapter?.add(listItem)
		}
	}

	inner class Model
	{
		var m_playByPlay: PlayByPlay? = null
			private set

		fun updatePlayByPlay(playByPlay: PlayByPlay)
		{
			m_playByPlay = playByPlay
		}
	}

	companion object
	{
		fun newInstance(game: GameSummary): PlayByPlayFragment = PlayByPlayFragment().apply {
			m_game = game
		}
	}
}