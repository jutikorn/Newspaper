package com.github.ayltai.newspaper.main;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.mockito.Mockito;

import com.github.ayltai.newspaper.PresenterTest;
import com.github.ayltai.newspaper.data.Source;

import rx.subjects.PublishSubject;

public final class MainPresenterTest extends PresenterTest<MainPresenter, MainPresenter.View> {
    //region Constants

    private static final int    FAVORITE_COUNT = 3;
    private static final String SOURCE_NAME    = "SOURCE_NAME";
    private static final String SOURCE_URL     = "SOURCE_URL";

    //endregion

    //region Events

    private final PublishSubject<Integer> pageChanges    = PublishSubject.create();
    private final PublishSubject<Void>    previousClicks = PublishSubject.create();
    private final PublishSubject<Void>    nextClicks     = PublishSubject.create();

    //endregion

    private MainAdapter mainAdapter;

    @NonNull
    @Override
    protected MainPresenter createPresenter() {
        final Source source = Mockito.mock(Source.class);
        Mockito.doReturn(MainPresenterTest.SOURCE_NAME).when(source).getName();
        Mockito.doReturn(MainPresenterTest.SOURCE_URL).when(source).getUrl();

        Mockito.doReturn(source).when(this.mainAdapter).getSource(Mockito.anyInt());

        final MainPresenter presenter = Mockito.spy(new MainPresenter());
        Mockito.doReturn(this.mainAdapter).when(presenter).createMainAdapter();

        return presenter;
    }

    @NonNull
    @Override
    protected MainPresenter.View createView() {
        final MainPresenter.View view = Mockito.mock(MainPresenter.View.class);

        Mockito.doReturn(this.pageChanges).when(view).pageChanges();
        Mockito.doReturn(this.previousClicks).when(view).previousClicks();
        Mockito.doReturn(this.nextClicks).when(view).nextClicks();

        return view;
    }

    @Override
    public void setUp() throws Exception {
        this.mainAdapter = Mockito.mock(MainAdapter.class);
        Mockito.doReturn(MainPresenterTest.FAVORITE_COUNT).when(this.mainAdapter).getCount();

        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        this.getView().close();
        this.mainAdapter.close();

        super.tearDown();
    }

    //region Tests

    @Test
    public void testViewBinding() throws Exception {
        this.bind();

        Mockito.verify(this.getView(), Mockito.times(1)).bind(this.mainAdapter);
    }

    @Test
    public void testWhenBoundThenUpdateHeader() throws Exception {
        this.bind();

        Mockito.verify(this.getView(), Mockito.times(1)).updateHeaderImages(Mockito.anyList());
        Mockito.verify(this.getView(), Mockito.times(1)).updateHeaderTitle(MainPresenterTest.SOURCE_NAME);
        Mockito.verify(this.getView(), Mockito.times(1)).enablePrevious(false);
        Mockito.verify(this.getView(), Mockito.times(1)).enableNext(true);
    }

    @Test
    public void testWhenNavigationButtonClickedThenNavigate() throws Exception {
        this.bind();

        this.nextClicks.onNext(null);
        Mockito.verify(this.getView(), Mockito.times(1)).navigateNext();

        this.previousClicks.onNext(null);
        Mockito.verify(this.getView(), Mockito.times(1)).navigatePrevious();
    }

    //endregion

    private void bind() {
        this.getPresenter().bind();
        this.pageChanges.onNext(0);
    }
}