SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

INSERT INTO public.operation VALUES (1, 'RouteBackward', '<<', 'Backward', NULL, true, true, true, false);
INSERT INTO public.operation VALUES (7, 'RouteToBookings', 'Bookings', 'Bookings', NULL, true, true, false, false);
INSERT INTO public.operation VALUES (3, 'ChangeLanguageToEnglish', 'English', 'English', NULL, true, true, true, false);
INSERT INTO public.operation VALUES (6, 'RouteToEvents', 'Events', 'Events', NULL, true, true, false, false);
INSERT INTO public.operation VALUES (2, 'RouteForward', '>>', 'Forward', NULL, true, true, true, false);
INSERT INTO public.operation VALUES (4, 'ChangeLanguageToFrench', 'Français', 'French', NULL, true, true, true, false);
INSERT INTO public.operation VALUES (8, 'RouteToLetters', 'Letters', 'Letters', NULL, true, true, false, false);
INSERT INTO public.operation VALUES (9, 'RouteToMonitor', 'Monitor', 'Monitor', NULL, true, false, false, false);
INSERT INTO public.operation VALUES (12, 'RouteToNewBackendBooking', 'NewBooking', 'New backend booking', NULL, true, false, false, false);
INSERT INTO public.operation VALUES (13, 'RouteToOperations', 'Operations', 'Operations', NULL, true, false, false, false);
INSERT INTO public.operation VALUES (14, 'RouteToAuthorizations', 'Authorizations', 'Authorizations', NULL, true, false, false, false);
INSERT INTO public.operation VALUES (5, 'RouteToOrganizations', 'Organizations', 'Organizations', NULL, true, true, true, false);
INSERT INTO public.operation VALUES (10, 'RouteToTester', 'Tester', 'Tester', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (11, 'RouteToCloneEvent', 'CloneEvent', 'Clone event', NULL, true, false, false, false);
INSERT INTO public.operation VALUES (15, 'RouteToStatistics', 'Statistics', 'Statistics', NULL, true, true, true, false);
INSERT INTO public.operation VALUES (16, 'RouteToIncome', 'Income', 'Income', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (17, 'RouteToPayments', 'Payments', 'Payments', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (18, 'RouteToStatements', 'Statements', 'Statements', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (19, 'RouteToUsers', 'Users', 'Users', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (20, 'RouteToDiningAreas', 'DiningAreas', 'Dining areas', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (21, 'RouteToRoomsGraphic', 'RoomsGraphic', 'Rooms graphic', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (22, 'CopySelection', 'CopySelection', 'Copy selection', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (23, 'CopyAll', 'CopyAll', 'Copy all', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (24, 'SendLetter', 'SendLetter...', 'Send letter', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (25, 'ToggleMarkDocumentAsRead', 'MarkAsRead', 'Toggle mark document as read', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (26, 'ToggleMarkDocumentAsWillPay', 'WillPay', 'Toggle mark document as will pay', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (27, 'ToggleCancelDocument', 'ToggleCancel...', 'Toggle cancel document', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (28, 'ToggleConfirmDocument', 'Confirm...', 'Toggle confirm document', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (29, 'ToggleFlagDocument', 'Flag...', 'Toggle flag document', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (30, 'ToggleMarkDocumentPassAsReady', 'PassReady', 'Toggle mark document pass as ready', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (31, 'MarkDocumentPassAsUpdated', 'PassUpdated', 'Mark document pass as updated', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (32, 'ToggleMarkDocumentAsArrived', 'MarkAsArrived', 'Toogle mark document as arrived', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (33, 'ToggleMarkDocumentAsUnchecked', 'MarkAsUnchecked', 'Mark document as unchecked', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (34, 'ToggleMarkDocumentAsUnknown', 'MarkAsUnknown', 'Mark document as unknown', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (35, 'ToggleMarkDocumentAsKnown', 'MarkAsKnown', 'Mark document as known', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (36, 'ToggleMarkDocumentAsVerified', 'MarkAsVerified', 'Mark document as verified', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (37, 'EditDocumentPersonalDetails', 'Edit...', 'Edit document personal details', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (38, 'AddNewDocumentLine', 'Add...', 'Add new document line', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (39, 'EditDocumentLine', 'Edit...', 'Edit document line', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (40, 'ToggleCancelDocumentLine', 'ToggleCancel...', 'Toggle cancel document line', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (41, 'DeleteDocumentLine', 'Delete...', 'Delete document line', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (42, 'AddNewPayment', 'AddPayment...', 'Add new payment', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (43, 'AddNewTransfer', 'AddTransfer...', 'Add new transfer', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (44, 'EditPayment', 'Edit...', 'Edit payment', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (45, 'DeletePayment', 'Delete...', 'Delete payment', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (46, 'MergeMultipleBookingsOptions', 'MergeOptionsFromOtherNotCancelledBookings...', 'Merge multiple bookings options', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (47, 'CancelOtherMultipleBookings', 'CancelOthers...', 'Cancel other multiple bookings', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (48, 'GetBackCancelledMultipleBookingsDeposit', 'GetBackCancelledDeposit...', 'Get back deposit from cancelled multiple bookings', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (49, 'ToggleMarkMultipleBooking', 'UnmarkAsMultipleBooking...', 'Toggle mark multiple bookings', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (50, 'OpenMail', 'OpenMail...', 'Open mail', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (51, 'ComposeNewMail', 'ComposeNewMail...', 'Compose new mail', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (52, 'OpenBookingCart', 'OpenBookingCart', 'Open booking cart', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (53, 'ToggleResourceConfigurationOnlineOffline', 'ToggleOnlineOffline', 'Toggle online/offline', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (54, 'EditResourceConfigurationProperties', 'EditRoomProperties...', 'Edit resource configuration properties', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (55, 'DeleteResource', 'DeleteRoom...', 'Delete resource', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (56, 'ChangeResourceConfigurationItem', 'ChangeRoomType...', 'Change resource configuration item', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (57, 'AddNewAllocationRule', 'Add...', 'Add new allocation rule', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (58, 'TriggerAllocationRule', 'Trigger...', 'Trigger allocation rule', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (59, 'EditAllocationRule', 'Edit...', 'Edit allocation rule', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (60, 'DeleteAllocationRule', 'Delete...', 'Delete allocation rule', NULL, true, false, true, false);
INSERT INTO public.operation VALUES (61, 'RouteToFilters', 'Filters', 'Filter builder', NULL, true, true, true, false);
