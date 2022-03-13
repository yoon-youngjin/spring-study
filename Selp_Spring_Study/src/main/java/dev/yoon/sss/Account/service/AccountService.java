package dev.yoon.sss.Account.service;

import dev.yoon.sss.Account.domain.Account;
import dev.yoon.sss.Account.dto.AccountDto;
import dev.yoon.sss.Account.exception.AccountNotFoundException;
import dev.yoon.sss.Account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(AccountDto.SignUpReq dto) {
        return accountRepository.save(dto.toEntity());
    }

    public Account findById(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        account.orElseThrow(() -> new AccountNotFoundException(id));

        return account.get();
    }

    public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
        final Account account = findById(id);

//        if(dto.)
        account.updateMyAccount(dto);
        return account;
    }
}
