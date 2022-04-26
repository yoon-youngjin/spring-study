package dev.yoon.springguide.domain.member.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MemberPredicateExecutor<T> extends QuerydslPredicateExecutor<T> {

}
