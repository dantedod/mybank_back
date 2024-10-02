package com.example.my_bank_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_bank_backend.domain.transference.Transference;

public interface TransferenceRepository extends JpaRepository<Transference, Long> {
    List<Transference> findBySenderAccountIdOrReceiverAccountId(Long senderAccountId, Long receiverAccountId);

}
