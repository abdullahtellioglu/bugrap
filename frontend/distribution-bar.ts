import { customElement } from 'lit/decorators.js';
import {html, LitElement} from "lit";

@customElement("distribution-bar")
export class DistributionBar extends LitElement {
    closedCount: number = 0;
    assignedCount: number = 0;
    unAssignedCount: number = 0;
    total: number = 0;
    setValues = (closed: number, assigned:number, unAssigned:number) => {
        let total = closed + assigned + unAssigned;
        this.closedCount = closed;
        this.assignedCount = assigned;
        this.unAssignedCount = unAssigned;
        this.total = total;
        this.requestUpdate();
    }
    render() {
        return html`
            <style>
                :host div {
                    height: 20px;
                    justify-content: start;
                    display: flex;
                    min-width: 30px;
                }
                :host div span {
                    color: white;
                    padding-left: 4px;
                    font-size: small;
                    font-weight: bold;
                }
                :host #closed {
                    background-color: var(--lumo-primary-color);
                }
                :host #assigned {
                    background-color: var(--lumo-secondary-color);
                }
                :host #un-assigned {
                    background-color: var(--lumo-tertiary-color);
                }
                :host .has-value:first-of-type {
                    border-bottom-left-radius: 4px;
                    border-top-left-radius: 4px;
                }
                :host .has-value:last-of-type {
                    border-bottom-right-radius: 4px;
                    border-top-right-radius: 4px;
                }
                :host .no-value {
                    display: none;
                }
            </style>
            <div>
                <div id="closed" style="width: ${(this.closedCount / this.total) * 100}%" class="closed ${this.closedCount > 0 ? 'has-value' : 'no-value'}">
                    <span>${this.closedCount}</span>
                </div>
                <div id="assigned" style="width: ${(this.assignedCount / this.total) * 100}%"  class="assigned ${this.assignedCount > 0 ? 'has-value' : 'no-value'}">
                    <span>${this.assignedCount}</span>
                </div>
                <div id="un-assigned" style="width: ${(this.unAssignedCount / this.total) * 100}%"  class="un-assigned ${this.unAssignedCount > 0 ? 'has-value' : 'no-value'}">
                    <span>${this.unAssignedCount}</span>
                </div>

            </div>`;
    }
}