import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TarefaListComponent } from './components/tarefa-list/tarefa-list.component';

@Component({
  selector: 'app-root',
  standalone: true, 
  imports: [
    CommonModule, 
    TarefaListComponent 
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'todo-list-app';
}