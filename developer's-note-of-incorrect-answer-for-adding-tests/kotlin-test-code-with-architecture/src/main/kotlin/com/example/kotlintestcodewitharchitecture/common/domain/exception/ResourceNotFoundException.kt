package com.example.kotlintestcodewitharchitecture.common.domain.exception

class ResourceNotFoundException : RuntimeException {
    constructor(datasource: String, id: Long) : super("$datasource 에서 ID $id 를 찾을 수 없습니다.")

    constructor(datasource: String, email: String) : super("$datasource 에서 ID $email 를 찾을 수 없습니다.")
}