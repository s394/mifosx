package org.mifosng.platform.loan.domain;

import java.util.List;

public class DefaultLoanLifecycleStateMachine implements
		LoanLifecycleStateMachine {

	private final List<LoanStatus> allowedLoanStatuses;

	public DefaultLoanLifecycleStateMachine(List<LoanStatus> allowedLoanStatuses) {
		this.allowedLoanStatuses = allowedLoanStatuses;
	}

	@Override
	public LoanStatus transition(LoanEvent loanEvent, LoanStatus from) {

		LoanStatus newState = null;

		switch (loanEvent) {
		case LOAN_CREATED:
			if (from == null) {
				newState = stateOf(LoanStatus.SUBMITED_AND_PENDING_APPROVAL,
						allowedLoanStatuses);
			}
			break;
		case LOAN_REJECTED:
			if (from.hasStateOf(LoanStatus.SUBMITED_AND_PENDING_APPROVAL)) {
				newState = stateOf(LoanStatus.REJECTED, allowedLoanStatuses);
			}
			break;
		case LOAN_APPROVED:
			if (from.hasStateOf(LoanStatus.SUBMITED_AND_PENDING_APPROVAL)) {
				newState = stateOf(LoanStatus.APPROVED, allowedLoanStatuses);
			}
			break;
		case LOAN_WITHDRAWN:
			if (this.anyOfAllowedWhenComingFrom(from, LoanStatus.SUBMITED_AND_PENDING_APPROVAL, LoanStatus.APPROVED)) {
				newState = stateOf(LoanStatus.WITHDRAWN_BY_CLIENT, allowedLoanStatuses);
			}
			break;
		case LOAN_DISBURSED:
			if (from.hasStateOf(LoanStatus.APPROVED)) {
				newState = stateOf(LoanStatus.ACTIVE, allowedLoanStatuses);
			}
			break;
		case LOAN_APPROVAL_UNDO:
			if (from.hasStateOf(LoanStatus.APPROVED)) {
				newState = stateOf(LoanStatus.SUBMITED_AND_PENDING_APPROVAL, allowedLoanStatuses);
			}
			break;
		case LOAN_DISBURSAL_UNDO:
			if (this.anyOfAllowedWhenComingFrom(from, LoanStatus.ACTIVE)) {
				newState = stateOf(LoanStatus.APPROVED, allowedLoanStatuses);
			}
			break;
		case LOAN_REPAYMENT:
			if (this.anyOfAllowedWhenComingFrom(from, LoanStatus.ACTIVE)) {
				newState = stateOf(LoanStatus.ACTIVE, allowedLoanStatuses);
			} else {
				newState = from;
			}
			break;
		case REPAID_IN_FULL:
			if (this.anyOfAllowedWhenComingFrom(from, LoanStatus.ACTIVE)) {
				newState = stateOf(LoanStatus.CLOSED, allowedLoanStatuses);
			}
			break;
		case LOAN_WRITE_OFF:
			if (this.anyOfAllowedWhenComingFrom(from,LoanStatus.ACTIVE)) {
				newState = stateOf(LoanStatus.CLOSED, allowedLoanStatuses);
			}
			break;
		case LOAN_RESCHEDULE:
			if (this.anyOfAllowedWhenComingFrom(from, LoanStatus.ACTIVE)) {
				newState = stateOf(LoanStatus.CLOSED, allowedLoanStatuses);
			}
			break;
		case INTERST_REBATE_OWED:
			if (this.anyOfAllowedWhenComingFrom(from, LoanStatus.CLOSED)) {
				newState = stateOf(LoanStatus.CLOSED, allowedLoanStatuses);
			}
			break;
		}

		return newState;
	}

	private LoanStatus stateOf(Integer state, List<LoanStatus> allowedLoanStatuses) {
		LoanStatus match = null;
		for (LoanStatus loanStatus : allowedLoanStatuses) {
			if (loanStatus.hasStateOf(state)) {
				match = loanStatus;
				break;
			}
		}
		return match;
	}

	private boolean anyOfAllowedWhenComingFrom(final LoanStatus state,
			final Integer... allowedStates) {
		boolean allowed = false;

		for (Integer allowedState : allowedStates) {
			if (state.hasStateOf(allowedState)) {
				allowed = true;
				break;
			}
		}

		return allowed;
	}

}