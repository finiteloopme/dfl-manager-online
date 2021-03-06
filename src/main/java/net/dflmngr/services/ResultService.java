package net.dflmngr.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dflmngr.model.entities.AflPlayer;
import net.dflmngr.model.entities.DflFixture;
import net.dflmngr.model.entities.DflPlayer;
import net.dflmngr.model.entities.DflPlayerPredictedScores;
import net.dflmngr.model.entities.DflPlayerScores;
import net.dflmngr.model.entities.DflSelectedPlayer;
import net.dflmngr.model.entities.DflTeam;
import net.dflmngr.model.entities.DflTeamPredictedScores;
import net.dflmngr.model.entities.DflTeamScores;
import net.dflmngr.model.entities.RawPlayerStats;
import net.dflmngr.model.entities.keys.DflFixturePK;
import net.dflmngr.model.entities.keys.DflPlayerPredictedScoresPK;
import net.dflmngr.model.entities.keys.DflPlayerScoresPK;
import net.dflmngr.model.entities.keys.DflTeamPredictedScoresPK;
import net.dflmngr.model.entities.keys.DflTeamScoresPK;
import net.dflmngr.model.web.PlayerStats;
import net.dflmngr.model.web.Results;
import net.dflmngr.model.web.SelectedPlayer;
import net.dflmngr.model.web.TeamResults;
import net.dflmngr.repositories.AflPlayerRepository;
import net.dflmngr.repositories.DflFixtureRepository;
import net.dflmngr.repositories.DflPlayerPredictedScoresRepository;
import net.dflmngr.repositories.DflPlayerRepository;
import net.dflmngr.repositories.DflPlayerScoresRepository;
import net.dflmngr.repositories.DflSelectedPlayerRepository;
import net.dflmngr.repositories.DflTeamPredictedScoresRepository;
import net.dflmngr.repositories.DflTeamRepository;
import net.dflmngr.repositories.DflTeamScoresRepository;
import net.dflmngr.repositories.RawPlayerStatsRepository;

@Service
public class ResultService {

	private final DflFixtureRepository dflFixtureRepository;
	private final DflTeamRepository dflTeamRepository;
	private final DflPlayerRepository dflPlayerRepository;
	private final AflPlayerRepository aflPlayerRepository;
	private final DflSelectedPlayerRepository dflSelectedPlayerRepository;
	private final RawPlayerStatsRepository rawPlayerStatsRepository;
	private final DflPlayerScoresRepository dflPlayerScoresRepository;
	private final DflPlayerPredictedScoresRepository dflPlayerPredictedScoresRepository;
	private final DflTeamScoresRepository dflTeamScoresRepository;
	private final DflTeamPredictedScoresRepository dflTeamPredictedScoresRepository;
	
	
	@Autowired
	public ResultService(DflFixtureRepository dflFixtureRepository, DflTeamRepository dflTeamRepository, DflPlayerRepository dflPlayerRepository, AflPlayerRepository aflPlayerRepository,
			             DflSelectedPlayerRepository dflSelectedPlayerRepository, RawPlayerStatsRepository rawPlayerStatsRepository, DflPlayerScoresRepository dflPlayerScoresRepository,
			             DflPlayerPredictedScoresRepository dflPlayerPredictedScoresRepository, DflTeamScoresRepository dflTeamScoresRepository,
			             DflTeamPredictedScoresRepository dflTeamPredictedScoresRepository) {
		this.dflFixtureRepository = dflFixtureRepository;
		this.dflTeamRepository = dflTeamRepository;
		this.dflPlayerRepository = dflPlayerRepository;
		this.aflPlayerRepository = aflPlayerRepository;
		this.dflSelectedPlayerRepository = dflSelectedPlayerRepository;
		this.rawPlayerStatsRepository = rawPlayerStatsRepository;
		this.dflPlayerScoresRepository = dflPlayerScoresRepository;
		this.dflPlayerPredictedScoresRepository = dflPlayerPredictedScoresRepository;
		this.dflTeamScoresRepository = dflTeamScoresRepository;
		this.dflTeamPredictedScoresRepository = dflTeamPredictedScoresRepository;
	}
	
	public Results getResults(int round, int game) {
		
		Results results = new Results();
		results.setRound(round);
		results.setGame(game);
		
		DflFixture dflFixture = getFixture(round, game);
		String homeTeamCode = dflFixture.getHomeTeam();
		String awayTeamCode = dflFixture.getAwayTeam();
		
		results.setHomeTeam(getTeamResults(round, homeTeamCode));
		results.setAwayTeam(getTeamResults(round, awayTeamCode));
			
		return results;
	}
	
	private TeamResults getTeamResults(int round, String teamCode) {
		
		System.out.println("Round: " + round + " Team: " + teamCode);
		
		TeamResults teamResults = new TeamResults();
		
		DflTeam team = dflTeamRepository.findOne(teamCode);
		teamResults.setTeamCode(teamCode);
		teamResults.setTeamName(team.getName());
		
		List<DflSelectedPlayer> selectedTeam = dflSelectedPlayerRepository.findByRoundAndTeamCode(round, teamCode);
		List<SelectedPlayer> players = new ArrayList<>();
		List<SelectedPlayer> emergencies = new ArrayList<>();
		
		for(DflSelectedPlayer selectedPlayer : selectedTeam) {
			if(selectedPlayer.isScoreUsed()) {
				players.add(getSelectedPlayer(selectedPlayer));
			} else {
				emergencies.add(getSelectedPlayer(selectedPlayer));
			}
		}
		
		teamResults.setPlayers(players);
		teamResults.setEmergencies(emergencies);
		
		DflTeamScoresPK dflTeamScoresPK = new DflTeamScoresPK();
		dflTeamScoresPK.setRound(round);
		dflTeamScoresPK.setTeamCode(teamCode);
		DflTeamScores dflTeamScore = dflTeamScoresRepository.findOne(dflTeamScoresPK);
		
		DflTeamPredictedScoresPK dflTeamPredictedScoresPK = new DflTeamPredictedScoresPK();
		dflTeamPredictedScoresPK.setRound(round);
		dflTeamPredictedScoresPK.setTeamCode(teamCode);
		DflTeamPredictedScores dflTeamPredictedScore = dflTeamPredictedScoresRepository.findOne(dflTeamPredictedScoresPK);
		
		System.out.println("Predicted Scores: " + dflTeamPredictedScore);
		
		teamResults.setScore(dflTeamScore.getScore());
		teamResults.setPredictedScore(dflTeamPredictedScore.getPredictedScore());
		
		return teamResults;
	}
	
	private SelectedPlayer getSelectedPlayer(DflSelectedPlayer selectedPlayer) {
		SelectedPlayer sp = new SelectedPlayer();
		
		sp.setPlayerId(selectedPlayer.getPlayerId());
		sp.setTeamPlayerId(selectedPlayer.getTeamPlayerId());
		
		DflPlayer player = dflPlayerRepository.findOne(selectedPlayer.getPlayerId());
		
		if(player.getInitial() == null) {
			sp.setName(player.getFirstName() + " " + player.getLastName());
		} else {
			sp.setName(player.getFirstName() + " " + player.getInitial() + ". " + player.getLastName());
		}
		
		sp.setPosition(player.getPosition());
		sp.setHasPlayer(selectedPlayer.hasPlayed());
		sp.setScoreUsed(selectedPlayer.isScoreUsed());
		sp.setDnp(selectedPlayer.isDnp());
		sp.setReplacementInd(selectedPlayer.getReplacementInd());
		
		sp.setStats(getPlayerStats(selectedPlayer.getRound(), selectedPlayer.getPlayerId()));
		
		return sp;
	}
	
	private PlayerStats getPlayerStats(int round, int playerId) {
		PlayerStats playerStats = new PlayerStats();
		
		AflPlayer aflPlayer = aflPlayerRepository.findByDflPlayerId(playerId);
				
		RawPlayerStats rawPlayerStats = rawPlayerStatsRepository.findByRoundAndTeamAndJumperNo(round, aflPlayer.getTeamId(), aflPlayer.getJumperNo());
		
		playerStats.setKicks(rawPlayerStats.getKicks());
		playerStats.setHandballs(rawPlayerStats.getHandballs());
		playerStats.setDisposals(rawPlayerStats.getDisposals());
		playerStats.setMarks(rawPlayerStats.getMarks());
		playerStats.setHitouts(rawPlayerStats.getHitouts());
		playerStats.setFreesFor(rawPlayerStats.getFreesFor());
		playerStats.setFreesAgainst(rawPlayerStats.getFreesAgainst());
		playerStats.setTackles(rawPlayerStats.getTackles());
		playerStats.setGoals(rawPlayerStats.getGoals());
		playerStats.setBehinds(rawPlayerStats.getBehinds());
		
		DflPlayerScoresPK dflPlayerScoresPK = new DflPlayerScoresPK();
		dflPlayerScoresPK.setPlayerId(playerId);
		dflPlayerScoresPK.setRound(round);
		
		DflPlayerScores dflPlayerScores = dflPlayerScoresRepository.findOne(dflPlayerScoresPK);
		
		DflPlayerPredictedScoresPK dflPlayerPredictedScoresPK = new DflPlayerPredictedScoresPK();
		dflPlayerPredictedScoresPK.setPlayerId(playerId);
		dflPlayerPredictedScoresPK.setRound(round);
		
		DflPlayerPredictedScores dflPlayerPredictedScores = dflPlayerPredictedScoresRepository.findOne(dflPlayerPredictedScoresPK);
		
		playerStats.setScore(dflPlayerScores.getScore());
		playerStats.setPredictedScore(dflPlayerPredictedScores.getPredictedScore());
		
		return playerStats;
	}
		
	private DflFixture getFixture(int round, int game) {
		DflFixturePK pk = new DflFixturePK();
		pk.setRound(round);
		pk.setGame(game);
		
		DflFixture fixture = dflFixtureRepository.findOne(pk);
		
		return fixture;
	}
	
	

}
