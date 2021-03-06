package net.dflmngr.model.web;

import java.util.List;

public class TeamResults {

	private String teamCode;
	private String teamName;
	private List<SelectedPlayer> players;
	private List<SelectedPlayer> emergencies;
	private int score;
	private int predictedScore;
	private boolean emergencyUsed;
	
	public TeamResults() {}
	
	public TeamResults(String teamCode, String teamName, List<SelectedPlayer> players,
			List<SelectedPlayer> emergencies, int score, int predictedScore, boolean emergencyUsed) {
		super();
		this.teamCode = teamCode;
		this.teamName = teamName;
		this.players = players;
		this.emergencies = emergencies;
		this.score = score;
		this.predictedScore = predictedScore;
		this.emergencyUsed = emergencyUsed;
	}

	public String getTeamCode() {
		return teamCode;
	}
	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public List<SelectedPlayer> getPlayers() {
		return players;
	}
	public void setPlayers(List<SelectedPlayer> players) {
		this.players = players;
	}
	public List<SelectedPlayer> getEmergencies() {
		return emergencies;
	}
	public void setEmergencies(List<SelectedPlayer> emergencies) {
		this.emergencies = emergencies;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getPredictedScore() {
		return predictedScore;
	}
	public void setPredictedScore(int predictedScore) {
		this.predictedScore = predictedScore;
	}
	public boolean isEmergencyUsed() {
		return emergencyUsed;
	}
	public void setEmergencyUsed(boolean emergencyUsed) {
		this.emergencyUsed = emergencyUsed;
	}

	@Override
	public String toString() {
		return "TeamResults [teamCode=" + teamCode + ", teamName=" + teamName + ", players=" + players
				+ ", emergencies=" + emergencies + ", score=" + score + ", predictedScore=" + predictedScore
				+ ", emergencyUsed=" + emergencyUsed + "]";
	}
}
