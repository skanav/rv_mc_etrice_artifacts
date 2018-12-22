# eTrice CBMD example model

## Description

This example contains two main controllers which each communicate with two axis controllers. The axis controllers simulate movement of physical axis. 
Thus moving form position a to b takes some time.

Each of the main controllers continuously cycles two axis between two positions. ``Controller_ex1`` does assume that axis1 is always done moving before axis2. But, what if not?
The contract ``PAxisControlContract`` specifies the allowed message order between each axis controller and the main controllers. If ``Controller_ex1`` fulfills the contract it should be able to handle the specified messages.
Thus a model checker should generate the following warnings for ``Controller_ex1``:
- unhandled ``moveCompleted`` message from port ``toAxis2`` in state ``move1Requested`` and state ``move2Requested``
- unreachable transition ``tr4`` (message ``moveCompleted`` from port ``toAxis1`` in state ``axis1_move1_done``)

``Controller_ex2`` should be able fulfill the contract. Thus no warnings should be generated.

## Notes

- The AxisController uses a timing service to be able to simulated real axis movement. As there is no contract attached to the PTimer protocol, model checkers have to assume that any message order is acceptable on that port.