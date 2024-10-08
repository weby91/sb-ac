// Generated by Dagger (https://dagger.dev).
package jp.speakbuddy.edisonandroidexercise.use_case;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;
import jp.speakbuddy.edisonandroidexercise.repository.repository.FactRepository;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class GetSavedFactsUseCase_Factory implements Factory<GetSavedFactsUseCase> {
  private final Provider<FactRepository> repositoryProvider;

  public GetSavedFactsUseCase_Factory(Provider<FactRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetSavedFactsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetSavedFactsUseCase_Factory create(Provider<FactRepository> repositoryProvider) {
    return new GetSavedFactsUseCase_Factory(repositoryProvider);
  }

  public static GetSavedFactsUseCase newInstance(FactRepository repository) {
    return new GetSavedFactsUseCase(repository);
  }
}
