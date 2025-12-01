import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms'; 
import { Tarefa } from '../../models/tarefa.model';
import { TarefaService } from '../../services/tarefa.service';

@Component({
  selector: 'app-tarefa-form',
  standalone: true, 
  imports: [CommonModule, ReactiveFormsModule], 
  templateUrl: './tarefa-form.component.html',
  styleUrls: ['./tarefa-form.component.css']
})
export class TarefaFormComponent implements OnInit {
  @Input() tarefa?: Tarefa | null;
  @Output() saved = new EventEmitter<void>();
  @Output() canceled = new EventEmitter<void>();

  form!: FormGroup;
  isEdit = false;
  errorMsg = '';

  constructor(private fb: FormBuilder, private srv: TarefaService) {}

  ngOnInit(): void {
    this.isEdit = !!this.tarefa;
    this.criarFormulario();
  }

  criarFormulario() {
    let dataFormatadaParaInput = '';
    if (this.tarefa?.dataLimite) {
      if (this.tarefa.dataLimite.includes('/')) {
        const partes = this.tarefa.dataLimite.split('/');
        if (partes.length === 3) {
          const [dia, mes, ano] = partes;
          dataFormatadaParaInput = `${ano}-${mes}-${dia}`;
        }
      } else {
        dataFormatadaParaInput = this.tarefa.dataLimite;
      }
    }

    let custoFormatado = '';
    if (this.tarefa?.custo !== undefined && this.tarefa?.custo !== null) {
      custoFormatado = this.formatarMoedaVisual(this.tarefa.custo);
    }

    this.form = this.fb.group({
      nome: [this.tarefa?.nome || '', Validators.required],
      custo: [custoFormatado, Validators.required],
      dataLimite: [dataFormatadaParaInput, Validators.required]
    });
  }

  onCustoInput(event: any) {
    const valorDigitado = event.target.value;
    
    const apenasDigitos = valorDigitado.replace(/\D/g, '');
    
    if (!apenasDigitos) {
      this.form.get('custo')?.setValue('');
      return;
    }

    const valorNumerico = parseFloat(apenasDigitos) / 100;

    const valorFormatado = this.formatarMoedaVisual(valorNumerico);
    
    this.form.get('custo')?.setValue(valorFormatado, { emitEvent: false });
  }

  formatarMoedaVisual(valor: number): string {
    return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  converterMoedaParaNumber(valorString: any): number | null {
    if (valorString === null || valorString === undefined || valorString === '') return null;
    
    if (typeof valorString === 'number') return valorString;

    const limpo = valorString.replace(/[^\d,]/g, '').replace(',', '.');
    const floatVal = parseFloat(limpo);
    
    return isNaN(floatVal) ? null : floatVal;
  }

  limpar() {
    this.form.reset();
    this.errorMsg = '';
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    
    const formValues = this.form.value;

    const dataInput = formValues.dataLimite;
    let dataParaBackend = dataInput;
    
    if (dataInput && dataInput.includes('-')) {
      const partes = dataInput.split('-');
      if (partes.length === 3) {
        const [ano, mes, dia] = partes;
        dataParaBackend = `${dia}/${mes}/${ano}`;
      }
    }

    const custoNumerico = this.converterMoedaParaNumber(formValues.custo);
    
    if (custoNumerico === null) {
      this.errorMsg = "Valor de custo inválido.";
      return;
    }

    const dto = {
      ...formValues,
      custo: custoNumerico,
      dataLimite: dataParaBackend
    };
    
    this.errorMsg = ''; 

    if (this.isEdit) {
      this.srv.editar(this.tarefa!.id!, dto).subscribe({
        next: () => { this.saved.emit(); },
        error: e => this.tratarErro(e)
      });
    } else {
      this.srv.criar(dto).subscribe({
        next: () => { this.saved.emit(); },
        error: e => this.tratarErro(e)
      });
    }
  }

  tratarErro(e: any) {
    console.error('Erro ao salvar:', e);
    if (e.error && typeof e.error === 'string') {
       this.errorMsg = e.error;
    } else if (e.error && e.error.message) {
       this.errorMsg = e.error.message;
    } else {
       this.errorMsg = 'Erro ao salvar. Verifique se o nome já existe.';
    }
  }

  cancel() {
    this.canceled.emit();
  }
}