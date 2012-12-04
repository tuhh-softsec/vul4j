package org.zanata.webtrans.client.presenter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import net.customware.gwt.presenter.client.EventBus;

import org.hamcrest.Matchers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zanata.webtrans.client.events.EditorPageSizeChangeEvent;
import org.zanata.webtrans.client.events.FilterViewEvent;
import org.zanata.webtrans.client.events.NotificationEvent;
import org.zanata.webtrans.client.events.RefreshPageEvent;
import org.zanata.webtrans.client.events.UserConfigChangeEvent;
import org.zanata.webtrans.client.events.WorkspaceContextUpdateEvent;
import org.zanata.webtrans.client.rpc.CachingDispatchAsync;
import org.zanata.webtrans.client.service.UserOptionsService;
import org.zanata.webtrans.client.view.EditorOptionsDisplay;
import org.zanata.webtrans.shared.model.DiffMode;
import org.zanata.webtrans.shared.model.UserWorkspaceContext;
import org.zanata.webtrans.shared.rpc.HasWorkspaceContextUpdateData;
import org.zanata.webtrans.shared.rpc.LoadOptionsAction;
import org.zanata.webtrans.shared.rpc.LoadOptionsResult;
import org.zanata.webtrans.shared.rpc.NavOption;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;

@Test(groups = { "unit-tests" })
public class EditorOptionsPresenterTest
{
   private EditorOptionsPresenter presenter;
   @Mock
   private EditorOptionsDisplay display;
   @Mock
   private EventBus eventBus;
   @Mock
   private UserWorkspaceContext userWorkspaceContext;
   @Mock
   private ValidationOptionsPresenter validationDetailsPresenter;
   @Mock
   private HasValue<Boolean> needReviewChk;
   @Mock
   private HasValue<Boolean> translatedChk;
   @Mock
   private HasValue<Boolean> untranslatedChk;
   @Captor
   private ArgumentCaptor<ValueChangeHandler<Boolean>> filterChangeHandlerCaptor;
   @Mock
   private CachingDispatchAsync dispatcher;
   @Captor
   private ArgumentCaptor<UserConfigChangeEvent> eventCaptor;
   @Mock
   private UserOptionsService userOptionsService;

   private UserConfigHolder configHolder = new UserConfigHolder();

   @BeforeMethod
   public void beforeMethod()
   {
      MockitoAnnotations.initMocks(this);
      when(display.getNeedReviewChk()).thenReturn(needReviewChk);
      when(display.getTranslatedChk()).thenReturn(translatedChk);
      when(display.getUntranslatedChk()).thenReturn(untranslatedChk);
      when(userOptionsService.getConfigHolder()).thenReturn(configHolder);

      presenter = new EditorOptionsPresenter(display, eventBus, userWorkspaceContext, validationDetailsPresenter, dispatcher, userOptionsService);
      verify(display).setListener(presenter);
   }

   @Test
   public void onBindWillRegisterHandlers()
   {
      // Given: user workspace context is not readonly
      when(userWorkspaceContext.hasReadOnlyAccess()).thenReturn(false);

      // When:
      presenter.onBind();

      // Then:
      verify(validationDetailsPresenter).bind();
      verify(display).getNeedReviewChk();
      verify(display).getTranslatedChk();
      verify(display).getUntranslatedChk();
      verify(needReviewChk).addValueChangeHandler(filterChangeHandlerCaptor.capture());
      verify(translatedChk).addValueChangeHandler(filterChangeHandlerCaptor.capture());
      verify(untranslatedChk).addValueChangeHandler(filterChangeHandlerCaptor.capture());

      verify(eventBus).addHandler(FilterViewEvent.getType(), presenter);
      verify(eventBus).addHandler(WorkspaceContextUpdateEvent.getType(), presenter);
      verify(display).setOptionsState(userOptionsService.getConfigHolder().getState());
   }

   @Test
   public void filterChangeHandlerWillFireEvent()
   {
      // Given: checkbox value as following
      when(needReviewChk.getValue()).thenReturn(true);
      when(translatedChk.getValue()).thenReturn(false);
      when(untranslatedChk.getValue()).thenReturn(true);
      presenter.onBind();
      verify(needReviewChk).addValueChangeHandler(filterChangeHandlerCaptor.capture());
      ValueChangeHandler<Boolean> handler = filterChangeHandlerCaptor.getValue();
      ArgumentCaptor<FilterViewEvent> eventCaptor = ArgumentCaptor.forClass(FilterViewEvent.class);

      // When: value change event happens
      handler.onValueChange(null);

      // Then:
      verify(eventBus).fireEvent(eventCaptor.capture());
      FilterViewEvent event = eventCaptor.getValue();
      assertThat(event.isCancelFilter(), Matchers.equalTo(false));
      assertThat(event.isFilterNeedReview(), Matchers.equalTo(true));
      assertThat(event.isFilterTranslated(), Matchers.equalTo(false));
      assertThat(event.isFilterUntranslated(), Matchers.equalTo(true));
   }

   @Test
   public void canSetReadOnlyOnWorkspaceUpdate()
   {
      // Given: project become inactive
      WorkspaceContextUpdateEvent workspaceContextUpdateEvent = new WorkspaceContextUpdateEvent(workplaceContextData(false));
      when(userWorkspaceContext.hasReadOnlyAccess()).thenReturn(true);
      when(userOptionsService.getConfigHolder()).thenReturn(configHolder);

      // When:
      presenter.onWorkspaceContextUpdated(workspaceContextUpdateEvent);

      // Then:
      verify(userWorkspaceContext).setProjectActive(false);
      assertThat(configHolder.isDisplayButtons(), Matchers.is(false));
      verify(display).setOptionsState(configHolder.getState());
      verify(eventBus).fireEvent(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);
   }

   private static HasWorkspaceContextUpdateData workplaceContextData(final boolean projectActive)
   {
      return new HasWorkspaceContextUpdateData()
      {
         @Override
         public boolean isProjectActive()
         {
            return projectActive;
         }
      };
   }

   @Test
   public void willSetOptionsBackOnFilterViewCancelEvent()
   {
      FilterViewEvent event = new FilterViewEvent(true, true, true, true);

      presenter.onFilterView(event);

      verify(untranslatedChk).setValue(event.isFilterUntranslated(), false);
      verify(translatedChk).setValue(event.isFilterTranslated(), false);
      verify(needReviewChk).setValue(event.isFilterNeedReview(), false);
   }

   @Test
   public void willDoNothingIfItsNotCancelEvent()
   {
      FilterViewEvent cancelEvent = new FilterViewEvent(true, true, true, false);

      presenter.onFilterView(cancelEvent);

      verifyZeroInteractions(untranslatedChk, translatedChk, needReviewChk);
   }

   @Test
   public void onPageSizeClick()
   {
      presenter.onPageSizeClick(99);

      assertThat(configHolder.getEditorPageSize(), Matchers.equalTo(99));
      ArgumentCaptor<EditorPageSizeChangeEvent> eventCaptor = ArgumentCaptor.forClass(EditorPageSizeChangeEvent.class);
      verify(eventBus).fireEvent(eventCaptor.capture());
      assertThat(eventCaptor.getValue().getPageSize(), Matchers.equalTo(99));
   }

   @Test
   public void onEnterSaveApprovedOptionChange()
   {
      configHolder.setEnterSavesApproved(false);

      presenter.onEnterSaveOptionChanged(true);

      assertThat(configHolder.isEnterSavesApproved(), Matchers.is(true));
      verify(eventBus).fireEvent(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);
   }

   @Test
   public void onEditorButtonsOptionChange()
   {
      configHolder.setDisplayButtons(false);

      presenter.onEditorButtonsOptionChanged(true);

      assertThat(configHolder.isDisplayButtons(), Matchers.is(true));
      verify(eventBus).fireEvent(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);
   }

   @Test
   public void onNavOptionChange()
   {
      configHolder.setNavOption(NavOption.FUZZY_UNTRANSLATED);

      presenter.onSelectionChange("", NavOption.FUZZY);

      assertThat(configHolder.getNavOption(), Matchers.equalTo(NavOption.FUZZY));
      verify(eventBus).fireEvent(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);
   }

   @Test
   public void onShowSaveApprovedWarningChanged()
   {
      configHolder.setShowSaveApprovedWarning(true);

      presenter.onShowSaveApprovedWarningChanged(false);

      assertThat(configHolder.isShowSaveApprovedWarning(), Matchers.equalTo(false));
      verify(eventBus).fireEvent(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);
   }

   @Test
   public void onSpellCheckOptionChanged()
   {
      configHolder.setSpellCheckEnabled(false);

      presenter.onSpellCheckOptionChanged(true);

      assertThat(configHolder.isSpellCheckEnabled(), Matchers.equalTo(true));
      verify(eventBus).fireEvent(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);
   }

   @Test
   public void onTMDisplayModeChanged()
   {
      configHolder.setTMDisplayMode(DiffMode.HIGHLIGHT);

      presenter.onTransMemoryDisplayModeChanged(DiffMode.NORMAL);

      assertThat(configHolder.getTMDisplayMode(), Matchers.equalTo(DiffMode.NORMAL));
      verify(eventBus).fireEvent(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);
   }

   @Test
   public void onCodeMirrorOptionChanged()
   {
      configHolder.setUseCodeMirrorEditor(false);

      presenter.onUseCodeMirrorOptionChanged(true);

      assertThat(configHolder.isUseCodeMirrorEditor(), Matchers.equalTo(true));
      verify(eventBus).fireEvent(RefreshPageEvent.REDRAW_PAGE_EVENT);
   }

   @Test
   public void onLoadDefaultOptions()
   {
      when(needReviewChk.getValue()).thenReturn(false);
      when(translatedChk.getValue()).thenReturn(false);
      when(untranslatedChk.getValue()).thenReturn(false);

      presenter.loadDefaultOptions();

      verify(userOptionsService).loadEditorDefaultOptions();

      verify(display).setOptionsState(isA(UserConfigHolder.ConfigurationState.class));
      verify(eventBus).fireEvent(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);
      verify(eventBus).fireEvent(isA(NotificationEvent.class));
   }

   @Test
   public void onPersistOption()
   {
      configHolder.setFilterByNeedReview(true);
      configHolder.setFilterByTranslated(false);
      configHolder.setFilterByUntranslated(true);

      presenter.persistOptionChange();

      verify(userOptionsService).persistOptionChange(userOptionsService.getEditorOptions());
   }

   @Test
   public void onLoadSavedOption()
   {
      UserConfigHolder configHolder = new UserConfigHolder();
      configHolder.setEnterSavesApproved(true);
      configHolder.setFilterByTranslated(true);
      configHolder.setNavOption(NavOption.FUZZY);
      configHolder.setEditorPageSize(10);

      LoadOptionsResult result = new LoadOptionsResult(configHolder.getState());

      presenter.loadOptions();

      ArgumentCaptor<LoadOptionsAction> actionCaptor = ArgumentCaptor.forClass(LoadOptionsAction.class);
      ArgumentCaptor<AsyncCallback> callbackCaptor = ArgumentCaptor.forClass(AsyncCallback.class);
      verify(dispatcher).execute(actionCaptor.capture(), callbackCaptor.capture());

      AsyncCallback callback = callbackCaptor.getValue();

      when(needReviewChk.getValue()).thenReturn(false);
      when(translatedChk.getValue()).thenReturn(true);
      when(untranslatedChk.getValue()).thenReturn(false);
      callback.onSuccess(result);
      assertThat(configHolder.getEditorPageSize(), Matchers.equalTo(10));
      assertThat(configHolder.getNavOption(), Matchers.equalTo(NavOption.FUZZY));

      verify(eventBus).fireEvent(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);

      callback.onFailure(null);
      verify(eventBus, times(2)).fireEvent(isA(NotificationEvent.class));
   }
}
