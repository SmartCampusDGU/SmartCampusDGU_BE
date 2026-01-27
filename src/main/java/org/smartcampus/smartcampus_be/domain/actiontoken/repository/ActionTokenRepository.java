package org.smartcampus.smartcampus_be.domain.actiontoken.repository;

import org.smartcampus.smartcampus_be.domain.actiontoken.entity.ActionToken;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActionTokenRepository extends JpaRepository<ActionToken, Long> {

    Optional<ActionToken> findByToken(String token);

    Optional<ActionToken> findByOutlierLogAndMember(OutlierLog outlierLog, Member member);

    boolean existsByOutlierLogAndMemberAndUsedFalse(OutlierLog outlierLog, Member member);
}
