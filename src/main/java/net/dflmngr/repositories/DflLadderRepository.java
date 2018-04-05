package net.dflmngr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.dflmngr.model.entities.DflLadder;
import net.dflmngr.model.entities.keys.DflLadderPK;

@Repository
public interface DflLadderRepository extends JpaRepository<DflLadder, DflLadderPK> {
	
	@Query("select l from DflLadder l where l.round = (select max(round) from DflLadder)")
	public List<DflLadder> findCurrentDflLadder();
	
}
