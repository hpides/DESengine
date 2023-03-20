import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px

pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Scatter(name="DesisCen", x=[2, 10], mode='lines+markers'
                         , y=[1529252, 46757]
                         , line=dict(color='rgb(99,110,250)', width=2), marker=dict(size=5, symbol='circle')))
fig.add_trace(go.Scatter(name="DesisSW", x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[2910000.75, 869738.37, 486379.3, 203258.57, 53545.05, 13893.87]
                         , line=dict(color='rgb(255,161,90)', width=2), marker=dict(size=5, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Desis", x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[7057527.24, 7124118.93, 7474829, 7465423.46, 7278250.2, 7336149.25]
                         , line=dict(color='rgb(171,99,250)', width=2), marker=dict(size=5, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))




#legend
fig.update_layout(
    legend=dict(
        yanchor="top",
        y=0.99,
        xanchor="left",
        x=0.01,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=10,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="events/sec",
        titlefont=dict(size=15),
        ticktext=["0", "2M", "4M", "6M", "8M"],
        tickvals=[0, 2000000, 4000000, 6000000, 8000000],
        range=[0,8000000],
        tickmode="array",
    ),
    xaxis=dict(
        title_text="queries",
        titlefont=dict(size=15),
        ticktext=["1", "10", '100', "1000"],
        tickvals=[1, 10, 100, 1000],
        range=[0,3],
        type="log"
    )
)

# size & color
fig.update_layout(
    autosize=False,
    width=500,
    height=500,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)'
)

# fig.update_yaxes(automargin=True)
# fig.update_yaxes(ticks="outside", tickwidth=1, tickcolor='black', ticklen=5)
fig.update_xaxes(showline=True, linewidth=1, linecolor='black', mirror=True)
fig.update_yaxes(showline=True, linewidth=1, linecolor='black', mirror=True)

fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2")

# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\Throughput1.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\Throughput1.svg")