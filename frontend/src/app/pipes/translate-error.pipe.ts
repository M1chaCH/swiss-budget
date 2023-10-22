import {Pipe, PipeTransform} from '@angular/core';
import {ErrorService} from "../services/error.service";
import {ErrorDto} from "../dtos/ErrorDto";

@Pipe({
  name: 'translateError'
})
export class TranslateErrorPipe implements PipeTransform {

  transform(error: ErrorDto): string {
    if (!error)
      return "unexpected error";
    return ErrorService.parseErrorMessage(error);
  }
}
