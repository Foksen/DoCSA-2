// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract MireaPaymentExample {
    address public owner;
    uint256 public tuitionWei;

    mapping(address => uint256) public balances;
    mapping(address => bool) public expelled;

    event Paid(address indexed student, uint256 amount);
    event Expelled(address indexed student);
    event Withdrawn(address indexed student, address indexed to, uint256 amount);
    event Refunded(address indexed student, uint256 amount);

    modifier onlyOwner() {
        require(msg.sender == owner, "only owner");
        _;
    }

    constructor(uint256 _tuitionWei) {
        owner = msg.sender;
        tuitionWei = _tuitionWei;
    }

    function pay() external payable {
        require(msg.value == tuitionWei, "must pay exact tuition");
        require(balances[msg.sender] == 0, "already paid");
        balances[msg.sender] = msg.value;
        emit Paid(msg.sender, msg.value);
    }

    function markExpelled(address student) external onlyOwner {
        expelled[student] = true;
        emit Expelled(student);
    }

    function withdrawByUniversity(address student) external onlyOwner {
        require(!expelled[student], "student expelled");
        uint256 amt = balances[student];
        require(amt > 0, "no funds");
        balances[student] = 0;
        (bool ok,) = owner.call{value:amt}("");
        require(ok, "transfer failed");
        emit Withdrawn(student, owner, amt);
    }

    function claimRefund() external {
        require(expelled[msg.sender], "you are not expelled");
        uint256 amt = balances[msg.sender];
        require(amt > 0, "no funds to refund");
        balances[msg.sender] = 0;
        (bool ok,) = msg.sender.call{value:amt}("");
        require(ok, "refund failed");
        emit Refunded(msg.sender, amt);
    }
}
