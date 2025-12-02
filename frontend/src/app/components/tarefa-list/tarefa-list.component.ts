import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Tarefa } from '../../models/tarefa.model';
import { TarefaService } from '../../services/tarefa.service';
import { TarefaFormComponent } from '../tarefa-form/tarefa-form.component';

@Component({
  selector: 'app-tarefa-list',
  standalone: true,
  imports: [CommonModule, TarefaFormComponent],
  templateUrl: './tarefa-list.component.html',
  styleUrls: ['./tarefa-list.component.css']
})
export class TarefaListComponent implements OnInit {
  tarefas: Tarefa[] = [];
  total = '0';
  showForm = false;
  formTarefa?: Tarefa | null;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';
  loading = false;

  tarefaParaExcluir: Tarefa | null = null;

  draggedTarefa: Tarefa | null = null;

  constructor(private srv: TarefaService) {}

  ngOnInit(): void {
    this.load();
  }

load() {
  this.loading = true;

  this.srv.listar().subscribe({
    next: (t) => {
      this.tarefas = (t || []).sort((a, b) => a.ordem - b.ordem);
      this.updateTotal();
      this.loading = false;
    },
    error: (err) => {
      console.error(err);
      this.loading = false;
    }
  });
}


  updateTotal() {
    const sum = this.tarefas.reduce((acc, t) => acc + t.custo, 0);
    this.total = sum.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  onDragStart(event: DragEvent, tarefa: Tarefa) {
    this.draggedTarefa = tarefa;
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move';
      setTimeout(() => (event.target as HTMLElement).classList.add('dragging'), 0);
    }
  }

  onDragEnd(event: DragEvent) {
    (event.target as HTMLElement).classList.remove('dragging');
    this.draggedTarefa = null;
  }

  onDragOver(event: DragEvent) {
    event.preventDefault(); 
  }

onDrop(event: DragEvent, tarefaAlvo: Tarefa) {
  event.preventDefault();
  if (!this.draggedTarefa || this.draggedTarefa.id === tarefaAlvo.id) return;

  const origemIdx = this.tarefas.indexOf(this.draggedTarefa);
  const destinoIdx = this.tarefas.indexOf(tarefaAlvo);

  this.tarefas.splice(origemIdx, 1);
  this.tarefas.splice(destinoIdx, 0, this.draggedTarefa);

  const novaOrdem = destinoIdx + 1;

  this.srv.updateOrdem(this.draggedTarefa.id!, novaOrdem).subscribe({
    next: () => this.load(),
    error: (e) => {
      alert("Erro ao atualizar ordem.");
      this.load(); 
    }
  });
}

recalcularOrdensESalvar() {
  const requests = this.tarefas.map((t, index) => {
    const novaOrdem = index + 1;
    if (t.ordem !== novaOrdem) {
      t.ordem = novaOrdem;
      return this.srv.updateOrdem(t.id!, novaOrdem).pipe(
        catchError(err => {
          return of(null);
        })
      );
    } else {
      return of(null);
    }
  });
  forkJoin(requests).subscribe({
    next: () => this.load(),
    error: (e) => {
      this.load();
    }
  });
}

  mover(tarefa: Tarefa, direcao: 'up' | 'down') {
    const idx = this.tarefas.indexOf(tarefa);
    if (idx < 0) return;
    if (direcao === 'up' && idx === 0) return;
    if (direcao === 'down' && idx === this.tarefas.length - 1) return;

    const trocaIdx = direcao === 'up' ? idx - 1 : idx + 1;
    const temp = this.tarefas[trocaIdx];
    this.tarefas[trocaIdx] = this.tarefas[idx];
    this.tarefas[idx] = temp;

    this.recalcularOrdensESalvar();
  }
  abrirCriar() {
    this.formTarefa = null;
    this.showForm = true;
  }

  abrirEditar(t: Tarefa) {
    this.formTarefa = { ...t };
    this.showForm = true;
  }



mostrarToast(msg: string, type: 'success' | 'error' = 'success') {
  this.toastMessage = msg;
  this.toastType = type;

  setTimeout(() => (this.toastMessage = null), 3000);
}


onSaved() {
  this.showForm = false;
  this.load();
  this.mostrarToast(this.formTarefa ? 'Tarefa atualizada com sucesso!' : 'Tarefa criada com sucesso!');
}

  onCanceled() {
    this.showForm = false;
  }


  confirmarExcluir(t: Tarefa) {
    this.tarefaParaExcluir = t;
  }

efetivarExclusao() {
  if (!this.tarefaParaExcluir) return;

  this.srv.excluir(this.tarefaParaExcluir.id!).subscribe({
    next: () => {
      this.load();
      this.mostrarToast('Tarefa excluída com sucesso!', 'success'); 
      this.tarefaParaExcluir = null;
    },
    error: (e) => {
      this.mostrarToast('Erro ao excluir tarefa', 'error');
      this.tarefaParaExcluir = null;
    }
  });
}

  cancelarExclusao() {
    this.tarefaParaExcluir = null;
  }
}