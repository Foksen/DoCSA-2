
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;
import "remix_tests.sol";
import "../contracts/MireaPaymentExample.sol";

contract MireaPaymentExampleTest {
    MireaPaymentExample contractUnderTest;
    uint tuition = 1 ether;

    function beforeEach() public {
        contractUnderTest = new MireaPaymentExample(tuition);
    }

    function testOwnerIsDeployer() public {
        Assert.equal(contractUnderTest.owner(), address(this), unicode"Владелец должен быть текущий контракт-тестер");
    }

    function testMarkExpelledEmitAndRead() public {
        address student = address(0x1234);
        contractUnderTest.markExpelled(student);
        Assert.ok(contractUnderTest.expelled(student), unicode"Студент должен быть отчислен");
    }
}
