package com.jakelauer.baseballtheater.experiences.gamelist

import android.preference.PreferenceManager
import com.jakelauer.baseballtheater.MlbDataServer.DataStructures.GameSummary
import com.jakelauer.baseballtheater.MlbDataServer.DataStructures.GameSummaryCollection
import com.jakelauer.baseballtheater.MlbDataServer.GameSummaryCreator
import com.jakelauer.baseballtheater.R
import com.jakelauer.baseballtheater.base.RefreshableListFragment
import com.jakelauer.baseballtheater.base.Syringe
import com.jakelauer.baseballtheater.common.listitems.EmptyListIndicator
import com.jakelauer.baseballtheater.experiences.gamelist.gamedetail.GameDetailActivity
import org.joda.time.DateTime
import java.util.*
import java.util.concurrent.ExecutionException


/**
 * Created by Jake on 10/20/2017.
 */
class GameListFragment : RefreshableListFragment<GameListFragment.Model>()
{
	private var m_date: DateTime by Syringe()

	override fun getLayoutResourceId(): Int = R.layout.game_list_fragment

	override fun createModel(): Model
	{
		return Model()
	}

	override fun loadData()
	{
		m_refreshView.isRefreshing = true

		val gsCreator = GameSummaryCreator()
		try
		{
			gsCreator.GetSummaryCollection(m_date) {
				getModel().updateGames(it)
				onDataLoaded()
			}
		}
		catch (e: ExecutionException)
		{
			e.printStackTrace()
		}
		catch (e: InterruptedException)
		{
			e.printStackTrace()
		}
	}

	private fun onDataLoaded()
	{
		m_refreshView.isRefreshing = false
		m_adapter?.clear()

		val games = getModel().getGames()
		if (games?.GameSummaries != null)
		{
			val prefs = PreferenceManager.getDefaultSharedPreferences(context)
			val favTeamCode = prefs.getString("behavior_favorite_team", "none")
			Collections.sort(games.GameSummaries, Comparator<GameSummary> { o1, o2 ->
				val o1FavTeam = if (o1.homeFileCode == favTeamCode || o1.awayFileCode == favTeamCode) 0 else 1
				val o2FavTeam = if (o2.homeFileCode == favTeamCode || o2.awayFileCode == favTeamCode) 0 else 1

				if (o1FavTeam == 0 || o2FavTeam == 0)
				{
					return@Comparator o1FavTeam - o2FavTeam
				}

				val o1NullSort = if (o1.linescore != null) 0 else 1
				val o2NullSort = if (o2.linescore != null) 0 else 1

				o1NullSort - o2NullSort
			})

			for (game in games.GameSummaries)
			{
				val isFavTeam = favTeamCode == game.awayFileCode || favTeamCode == game.homeFileCode
				val item = GameItem(GameItem.Model(game, isFavTeam))

				item.setClickListener({ _, _ ->
					context?.let {
						GameDetailActivity.startActivity(game, it)
					}
				})

				m_adapter?.add(item)
			}
		}
		else
		{
			context?.let {
				val emptyListItem = EmptyListIndicator(EmptyListIndicator.Model(it))
				m_adapter?.add(emptyListItem)
			}
		}
	}

	class Model
	{
		private var m_games: GameSummaryCollection? = null

		fun updateGames(data: GameSummaryCollection?)
		{
			m_games = data
		}

		fun getGames() = m_games
	}

	companion object
	{
		fun newInstance(date: DateTime): GameListFragment =
				GameListFragment().apply {
					m_date = date
				}
	}
}