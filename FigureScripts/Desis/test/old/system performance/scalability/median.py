import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px

pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Scatter(name="Central", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[1563426.905, 1559000.38, 1565001.108, 1558914.97, 1558007.2, 1555151.267, 1543917.05, 1566713.37]
                         , line=dict(color='rgb(100, 30, 22)', width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="Disco", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[40379, 81010, 121524, 162212, 202755, 243282, 283521, 324008]
                         , line=dict(color='rgb(21, 67, 97)', width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="Desis", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[7301659.16, 7439741, 7531010, 7641958, 7664154, 7681207, 7689210, 7688381.22]
                         , line=dict(color='rgb(21, 90, 50)', width=5), marker=dict(size=20, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))

#legend
fig.update_layout(
    legend=dict(
        orientation="h",
        yanchor="top",
        y=1.2,
        xanchor="left",
        x=0.1,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=30,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="events/sec",
        titlefont=dict(size=35),
        # ticktext=["0", "3M", "5M", "10M", "100M", "300M"],
        # tickvals=[0, 3000000, 5000000, 10000000, 100000000, 300000/00],
        tickmode="array",
        ticktext=["0", "2M", "4M", "6M", "8M"],
        tickvals=[0, 2000000, 4000000, 6000000, 8000000],
        range=[0, 8100000],
        tickfont=dict(size=35),
    ),
    xaxis=dict(
        title_text="local nodes",
        titlefont=dict(size=35),
        ticktext=["1", "2", "3", "4", "5", "6", "7", "8"],
        tickvals=[1, 2, 3, 4, 5, 6, 7, 8],
        range=[1,8],
        tickmode="array",
        ticks="inside",
        ticklen=20,
        tickwidth=5,
        tickfont=dict(size=35),
    )
)
# size & color
fig.update_layout(
    autosize=False,
    width=660,
    height=440,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')

fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")

# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\scalability\scalabilityM.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\scalability\scalabilityM.svg")